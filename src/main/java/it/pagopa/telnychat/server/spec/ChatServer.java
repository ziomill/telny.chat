package it.pagopa.telnychat.server.spec;

import it.pagopa.telnychat.server.impl.TelnyClientHandler;

import java.net.ServerSocket;
import java.util.Set;

/**
 * Operations to implement to create a new Chat Server
 */
public interface ChatServer
{

    void start(int port);
    void stop();
    void addClient(String nickname,
                   TelnyClientHandler client);
    void removeClient(String nickname);
    Set<String> getClients();
    boolean isNicknameTaken(String nickname);
    Set<String> getTopics();
    void createTopic(String topicKey);
    void joinTopic(String nickname,
                   String topicKey);
    void sendMessageOnTopic(String sender,
                            String topicName,
                            String message);
    void sendMessageToClient(String sender,
                             String recipient,
                             String message);

}
