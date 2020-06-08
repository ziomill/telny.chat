package it.pagopa.telnychat.server.commands.specs;

import it.pagopa.telnychat.server.spec.ClientHandler;

public interface ChatCommand
{
    boolean execute(ClientHandler handler,
                    String input);
}
