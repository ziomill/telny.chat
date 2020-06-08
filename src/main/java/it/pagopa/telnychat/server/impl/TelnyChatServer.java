package it.pagopa.telnychat.server.impl;

import it.pagopa.telnychat.server.commons.Topic;
import it.pagopa.telnychat.server.spec.ChatServer;
import it.pagopa.telnychat.server.spec.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple Chat Server, TCP based, with Multiclient support.
 * The server ships with Default Topic (BROADCAST_TOPIC) where
 * all Clients are automatically added. Messages sended on Broadcast
 * Topic are broadcasted to all connected clients.
 */
public class TelnyChatServer implements ChatServer
{
    private ServerSocket serverSocket;          // Server Communication Channel
    private Map<String, ClientHandler> clients; // List of connected Clients
    private Map<String, Topic> topics;          // List of topics

    public static final String BROADCAST_TOPIC = "BROADCAST_TOPIC";

    /**
     * Start Server on specified specific port.
     * @param port The port where Server listens.
     */
    @Override
    public void start(int port)
    {
        try
        {
            // Init
            clients = new HashMap<>();
            topics = new HashMap<>();
            // Create Socket
            serverSocket = new ServerSocket(port);
            // Create Broadcast Topic
            createTopic(BROADCAST_TOPIC);

            System.out.println("[Server] Telny Chat Server started. " + "\n" +
                               "[Server] Waiting for incoming connections on port " + port + " ...");
            // Wait for Clients
            while(true)
            {
                // Accept Client connection --> accept() is blocking
                Socket clientSocket = serverSocket.accept();
                // Start new Thread wrapping channel communication between this Client and Server
                ClientHandler clientHandler = new TelnyClientHandler(clientSocket,this);
                clientHandler.start();
            }
        }
        catch (SocketException ex)
        {
            // Raised when server stops
            System.out.println("[Server] Server stopped. Bye!");
        }
        catch (IOException ex)
        {
            System.err.println("Something goes wrong starting Server on Port: " + port + "\n" +
                               "\tError message: " + ex.getMessage());

        }
    }


    /**
     * Stop Server
     */
    @Override
    public void stop()
    {
        try
        {
            // Disconnect clients
            int connectedClients = clients.size();
            Map<String,ClientHandler> copy = Map.copyOf(clients);
            copy.values().stream().forEach(clientHandler ->
            {
                clientHandler.disconnect();
            });

            // Waiting for Handlers thread for disconnection
            int allHandlersStopped = 0;
            while (allHandlersStopped != connectedClients)
            {
                for(ClientHandler handler : copy.values())
                {
                    if(!handler.isRunning())
                    {
                        allHandlersStopped ++;
                    }
                }
            }
            // Close Server Socket
            serverSocket.close();
        }
        catch (IOException ex)
        {
            System.err.println("Something goes wrong stopping Server" + "\n" +
                               "\tError message: " + ex.getMessage());
        }
    }

    /**
     * Subscribe Client to Topic
     * @param nickname the Client to subscribe.
     * @param topicKey the destination Topic.
     */
    @Override
    public void joinTopic(String nickname,
                          String topicKey)
    {
        Topic topic = topics.get(topicKey);
        ClientHandler client = clients.get(nickname);
        topic.addObserver(client);
    }

    /**
     * Create new Topic.
     * @param topicKey The unique key of Topic.
     */
    @Override
    public void createTopic(String topicKey)
    {
        Topic topic = new Topic(topicKey);
        topics.put(topicKey,topic);
    }

    /**
     * Send a message to Clients with a subscription on given Topic.
     * @param sender The client author of the message.
     * @param topicName The destination Topic.
     * @param message The message.
     */
    @Override
    public void sendMessageOnTopic(String sender,
                                   String topicName,
                                   String message)
    {
        Topic topic = topics.get(topicName);
        topic.sendMessageToObservers(sender,message);
    }

    @Override
    public void sendMessageToClient(String sender,
                                    String recipient,
                                    String message)
    {
        ClientHandler clientHandler = clients.get(recipient);
        clientHandler.forwardMessageToClient(sender,message);
    }

    /**
     * Add client to the List of connected peers.
     * @param nickname The nickname of Client to add.
     * @param client
     */
    @Override
    public void addClient(String nickname,
                          TelnyClientHandler client)
    {
        clients.put(nickname,client);
        Topic broadcast = topics.get(BROADCAST_TOPIC);
        String message = "[Server] User '" + nickname + "' joined Telny Chat :-)";
        broadcast.sendMessageToObservers(nickname,message);
        System.out.println(message);
    }

    /**
     * Remove client from the List of connected peers.
     * @param nickname The nickname of Client to remove.
     */
    @Override
    public void removeClient(String nickname)
    {
        clients.remove(nickname);
        Topic broadcast = topics.get(BROADCAST_TOPIC);
        String message = "[Server] User '" + nickname + "' left Telny chat :-(";
        broadcast.sendMessageToObservers(nickname,message);
        System.out.println(message);
    }

    /**
     * Get List of connected Clients
     * @return The List of connected Clients
     */
    @Override
    public Set<String> getClients()
    {
        return clients.keySet();
    }

    /**
     * Get List of active Topics
     * @return The List of active Topics
     */
    @Override
    public Set<String> getTopics()
    {
        return topics.keySet();
    }

    /**
     * Check if a Client identifed with given Nickanme alredy exists.
     * @param nickname Nickname to check.
     * @return True, if a Client with given Nickname exists.
     */
    @Override
    public boolean isNicknameTaken(String nickname){
        return clients.containsKey(nickname);
    }

}
