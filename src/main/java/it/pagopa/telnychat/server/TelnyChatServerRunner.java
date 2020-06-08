package it.pagopa.telnychat.server;

import it.pagopa.telnychat.server.impl.TelnyChatServer;
import it.pagopa.telnychat.server.spec.ChatServer;

public class TelnyChatServerRunner
{
    public static void main(String[] args)
    {
        ChatServer server = new TelnyChatServer();
        server.start(10000);
    }
}
