package it.pagopa.telnychat.server.test;

import it.pagopa.telnychat.server.commons.Command;
import it.pagopa.telnychat.server.impl.TelnyChatServer;
import it.pagopa.telnychat.server.spec.ChatServer;
import it.pagopa.telnychat.server.test.client.TelnyChatClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Integration Test for Telny Chat Server
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Integration Test for Telny Chat Server")
public class TelnyChatServer_IT
{
    private final static String LOCALHOST = "127.0.0.1";
    private final static Integer PORT = 10000;

    private ChatServer server;

    @BeforeEach
    public void init()
    {
        new Thread(() ->
        {
            try
            {
                // Start Server on Port 10000
                server = new TelnyChatServer();
                server.start(10000);
            }
            catch (Exception ex)
            {
                Assertions.fail("Can't start server: " + ex.getMessage());
            }
        }).start();

        // Wait Server Starting
        boolean isListening = false;
        while (!isListening)
        {
            try
            {
                Socket clientSocket = new Socket(LOCALHOST, PORT);
                isListening = clientSocket.isConnected();
            }
            catch (Exception ex)
            {
                Assertions.fail(ex.getMessage());
            }
        }
    }

    @AfterEach
    public void teardown()
    {
        server.stop();
    }

    @Test
    @DisplayName("Client Connection")
    public void client_connect_ServerRespondsWithLoginMessage()
    {
        try
        {
            Socket socket = new Socket(LOCALHOST, PORT);
            TelnyChatClient client = new TelnyChatClient(socket);
            String loginMessage = client.getMessage();
            assertThat(loginMessage, containsString("Hello, nice to meet you! What's your (nick)name) ? :-)"));
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Client Login")
    public void client_login_ServerRespondsWithWelcomeMessage()
    {
        try
        {
            Socket socket = new Socket(LOCALHOST, PORT);
            TelnyChatClient client = new TelnyChatClient(socket);
            client.getMessage(); // login message
            client.sendMessage("mill");
            String welcomeMessage = client.getMessage();
            assertThat(welcomeMessage, containsString("[********** Server Welcome Messagge **********]"));
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Client send a message on the default 'BROADCAST' Topic")
    public void client_sendMessageOnTopic_OtherSubscriptedClientsReceiveMessage()
    {
        try
        {
            // Client "Jupiter" connect and join the Default "BROADCAST TOPIC"
            Socket jupiterSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientJupiter = new TelnyChatClient(jupiterSocket);
            clientJupiter.getMessage(); // login message
            clientJupiter.sendMessage("Jupiter");
            consumeWelcomeMessages(clientJupiter);

            // Client "Pluto" connect and join the Default "BROADCAST TOPIC"
            Socket plutoSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientPluto = new TelnyChatClient(plutoSocket);
            clientPluto.getMessage(); // login message
            clientPluto.sendMessage("Pluto");
            consumeWelcomeMessages(clientPluto);
            // Jupiter has received a message about Pluto Connection
            clientJupiter.getMessage();

            // Client "Moon" connect and join the Default "BROADCAST TOPIC"
            Socket moonSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientMoon = new TelnyChatClient(moonSocket);
            clientMoon.getMessage(); // login message
            clientMoon.sendMessage("Moon");
            consumeWelcomeMessages(clientMoon);
            // Jupiter has received a message about Moon Connection
            clientJupiter.getMessage();
            // Pluto has received a message about Moon Connection
            clientPluto.getMessage();

            // Client Pluto send Message over BROADCAST_TOPIC
            clientPluto.sendMessage("Hello!");
            String broadcastedMessage = "[Pluto] : Hello!";

            // Client Jupiter receive Moon's message
            String toJupiterMessage = clientJupiter.getMessage();
            assertThat(toJupiterMessage, containsString(broadcastedMessage));

            // Client Jupiter receive Moon's message
            String toMoonMessage = clientMoon.getMessage();
            assertThat(toMoonMessage, containsString(broadcastedMessage));
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Client send command to get List of Topics")
    public void client_sendCommand_GET_TOPICS_ServerRespondsWithListOfActiveTopics()
    {
        try
        {
            // Client "Jupiter" connect and join the Default "BROADCAST TOPIC"
            Socket jupiterSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientJupiter = new TelnyChatClient(jupiterSocket);
            clientJupiter.getMessage(); // login message
            clientJupiter.sendMessage("Jupiter");
            consumeWelcomeMessages(clientJupiter);
            // Send ___GET_TOPICS command
            clientJupiter.sendMessage(Command.GET_TOPICS.getValue());
            String getTopicsMessage = clientJupiter.getMessage();
            Assertions.assertEquals("Active Topics     : BROADCAST_TOPIC",getTopicsMessage);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Client send command to get List of clients")
    public void client_sendCommand_GET_CLIENTS_ServerRespondsWithListOfActiveClients()
    {
        try
        {
            // Client "Jupiter" connect and join the Default "BROADCAST TOPIC"
            Socket jupiterSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientJupiter = new TelnyChatClient(jupiterSocket);
            clientJupiter.getMessage(); // login message
            clientJupiter.sendMessage("Jupiter");
            consumeWelcomeMessages(clientJupiter);

            // Client "Pluto" connect and join the Default "BROADCAST TOPIC"
            Socket plutoSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientPluto = new TelnyChatClient(plutoSocket);
            clientPluto.getMessage(); // login message
            clientPluto.sendMessage("Pluto");
            consumeWelcomeMessages(clientPluto);

            // Send ___GET_TOPICS command
            clientPluto.sendMessage(Command.GET_CLIENTS.getValue());
            String getClientsMessage = clientPluto.getMessage();
            Assertions.assertEquals("Connected Clients : Jupiter,Pluto",getClientsMessage);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Client send command to disconnect")
    public void client_sendCommand_DISCONNECT_ServerRespondsWithListOfActiveClients()
    {
        try
        {
            // Client "Jupiter" connect and join the Default "BROADCAST TOPIC"
            Socket jupiterSocket = new Socket(LOCALHOST, PORT);
            TelnyChatClient clientJupiter = new TelnyChatClient(jupiterSocket);
            clientJupiter.getMessage(); // login message
            clientJupiter.sendMessage("Jupiter");
            consumeWelcomeMessages(clientJupiter);
            Assertions.assertEquals(1,server.getClients().size());
            // Send ___GET_TOPICS command
            clientJupiter.sendMessage(Command.DISCONNECT.getValue());
            Thread.sleep(2000);
            Assertions.assertEquals(0,server.getClients().size());
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());
        }
    }

    /**
     * Welcome Message is of 6 lines
     * @param client
     * @throws IOException
     */
    private void consumeWelcomeMessages(TelnyChatClient client) throws IOException
    {
        for(int i=0 ; i <= 5 ; i++)
        {
            String message = client.getMessage();
            System.out.println(message);
        }
    }


}
