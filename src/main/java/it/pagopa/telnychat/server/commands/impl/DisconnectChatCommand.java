package it.pagopa.telnychat.server.commands.impl;

import it.pagopa.telnychat.server.commands.specs.ChatCommand;
import it.pagopa.telnychat.server.spec.ClientHandler;

public class DisconnectChatCommand implements ChatCommand
{
    @Override
    public boolean execute(ClientHandler handler,
                           String input)
    {
        handler.disconnect();
        return true;
    }
}
