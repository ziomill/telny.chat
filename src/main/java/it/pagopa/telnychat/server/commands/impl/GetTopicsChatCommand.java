package it.pagopa.telnychat.server.commands.impl;

import it.pagopa.telnychat.server.commands.specs.ChatCommand;
import it.pagopa.telnychat.server.spec.ClientHandler;

import java.util.Set;
import java.util.stream.Collectors;

public class GetTopicsChatCommand implements ChatCommand
{
    @Override
    public boolean execute(ClientHandler handler,
                           String input)
    {
        Set<String> activeTopics = handler.getServer().getTopics();
        String activeTopicsReduced = activeTopics.stream().map(Object::toString).collect(Collectors.joining(","));
        handler.forwardMessageToClient("Server","Active Topics     : " + activeTopicsReduced);
        return true;
    }
}
