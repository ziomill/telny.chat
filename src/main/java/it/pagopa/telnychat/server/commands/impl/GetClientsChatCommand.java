package it.pagopa.telnychat.server.commands.impl;

import it.pagopa.telnychat.server.commands.specs.ChatCommand;
import it.pagopa.telnychat.server.spec.ClientHandler;

import java.util.Set;
import java.util.stream.Collectors;

public class GetClientsChatCommand implements ChatCommand
{
    @Override
    public boolean execute(ClientHandler handler,
                           String input)
    {
        Set<String> connectedClients = handler.getServer().getClients();
        String connectedClientsReduced = connectedClients.stream().map(Object::toString).collect(Collectors.joining(","));
        handler.forwardMessageToClient("Server","Connected Clients : " + connectedClientsReduced);
        return true;
    }
}
