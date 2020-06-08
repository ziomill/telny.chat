package it.pagopa.telnychat.server.spec;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Operations to implement to create a new Server's Client Handler
 */
public abstract class ClientHandler extends Thread implements PropertyChangeListener
{
    public abstract void login() throws IOException;
    public abstract void startChatSession() throws IOException;
    public abstract void forwardMessageToClient(String sender,
                                                String message);
    public abstract String whois();
    public abstract void disconnect();
    public abstract boolean isRunning();
    public abstract ChatServer getServer();
}
