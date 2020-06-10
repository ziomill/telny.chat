package it.pagopa.telnychat.server.impl;

import it.pagopa.telnychat.server.commands.ChatStrategy;
import it.pagopa.telnychat.server.commons.Command;
import it.pagopa.telnychat.server.spec.ChatServer;
import it.pagopa.telnychat.server.spec.ClientHandler;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  Class that rapresents a Communication Channel for a connected Client and the Server.
 *  The Low Level channel is established with ClientSocket.
 *  Nickname is a Unique key that identify connected Client on the Server.
 *
 *  Every Channel:
 *  1.  Is a Thread: Allow Server to support multi-client connections.
 *  2.  Is an observer. He can subscribe itself on an existing Topic, receiving all
 *      messages sended on it from other subscribers.
 *
 */
public class TelnyClientHandler extends ClientHandler
{

    private String nickname;        // Nickname of this Client
    private ChatServer server;      // Server instance
    private Socket clientSocket;    // Communication Channel Client - Server
    private BufferedReader reader;  // Server read : From the Client Output Channel
    private PrintWriter writer;     // Server write: To the Client Input Channel

    public TelnyClientHandler(Socket clientSocket,
                              TelnyChatServer server)
    {
        try
        {
            this.clientSocket = clientSocket;
            this.server = server;
            writer = new PrintWriter(clientSocket.getOutputStream(), true, Charset.forName("UTF-8"));
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),Charset.forName("UTF-8")));
        }
        catch(Exception ex)
        {
            System.err.println("Something goes wrong creating Communication Channel between Client and Server" + "\n" +
                               "\tError message: " + ex.getMessage());
            disconnect();
        }
    }

    @Override
    public void run()
    {
        try
        {
            // Login
            login();

            // Chat
            startChatSession();
        }
        catch (SocketException ex)
        {
            // Raised when server stops
            System.out.println("[Server] Client was disconnected because Server was stopped!");
        }
        catch(IOException ex)
        {
            System.err.println("Something goes wrong starting Communication Channel between Client and Server" + "\n" +
                               "\tError message: " + ex.getMessage());
            disconnect();
        }
    }

    @Override
    public void login() throws IOException
    {
        // First Connection --> Registering client on the Server with a nickname
        do {
            writer.println("Hello, nice to meet you! What's your (nick)name) ? :-)");
        }
        while (StringUtils.isBlank(nickname = reader.readLine()));

        // Check if choosen Nickname is already taken
        while (server.isNicknameTaken(nickname))
        {
            do {
                writer.println("I'm sorry but Nickname: " + nickname + " is already used by another User :-(. Please, choose a different one ...");
            }
            while (StringUtils.isBlank(nickname = reader.readLine()));
        }

        // First Connection --> Add current Client to connected clients list
        server.addClient(nickname,this);

        // Join Default BROADCAST Topic
        server.joinTopic(nickname,TelnyChatServer.BROADCAST_TOPIC);

        // Get list of Connected clients
        Set<String> connectedClients = server.getClients();
        String connectedClientsReduced = connectedClients.stream().map(Object::toString).collect(Collectors.joining(","));

        // Get list of Active Topics
        Set<String> activeTopics = server.getTopics();
        String activeTopicsReduced = activeTopics.stream().map(Object::toString).collect(Collectors.joining(","));

        // Server Welcome message
        writer.println("[********** Server Welcome Messagge **********]");
        writer.println("->Hello: '" + nickname + "'!");
        writer.println("->Active Topics     : " + activeTopicsReduced);
        writer.println("->Connected Clients : " + connectedClientsReduced);
        writer.println("->Let's start chatting...");
        writer.println("[*********************************************]");
    }

    @Override
    public void startChatSession() throws IOException
    {
        // Wait for this client messages
        String inputLine;
        while ((inputLine = reader.readLine()) != null)
        {
            Command command = Command.getENUM(inputLine).isPresent() ? Command.getENUM(inputLine).get() : Command.BROADCAST_TO_TOPIC;
            ChatStrategy strategy = new ChatStrategy();
            strategy.execute(command,this,inputLine);
        }
        System.out.println("Stopped Communication Handler beetween Server and Client: " + nickname + "\n" +
                           "Thread with ID: " + this.getId() + " was killed.");
    }

    @Override
    public void forwardMessageToClient(String sender,
                                       String message)
    {
        System.out.println("New message sended from: " + sender + " to: " + nickname);
        writer.println(message);
    }

    /**
     * Get nickname of the Client managed by this Handler.
     * @return the Nickname of the Client.
     */
    @Override
    public String whois() {
        return nickname;
    }

    /**
     * Client Handler is an Observer with a subscription on some Topics.
     * When a message is sended on a Topic where this Client Handler
     * have an active subscription, this method is triggered.
     * @param evt The event triggered by Topic where this Client Handler is subscripted.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        writer.println(evt.getNewValue());
    }

    /**
     * Kill this Handler after Socket closing.
     */
    @Override
    public void disconnect()
    {
        try
        {
            // Remove Clients from Active Clients List of server
            server.removeClient(nickname);
            // Close the Socket
            clientSocket.close();
        }
        catch(Exception ex)
        {
            System.err.println("Something goes wrong disconnecting Client: " + nickname + "\n" +
                               "\tError message: " + ex.getMessage());
        }
    }

    @Override
    public ChatServer getServer() {
        return server;
    }

}
