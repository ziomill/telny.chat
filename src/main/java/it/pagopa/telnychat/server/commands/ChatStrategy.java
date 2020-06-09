package it.pagopa.telnychat.server.commands;

import it.pagopa.telnychat.server.commands.impl.BroadcastToTopicChatCommand;
import it.pagopa.telnychat.server.commands.impl.DisconnectChatCommand;
import it.pagopa.telnychat.server.commands.impl.GetClientsChatCommand;
import it.pagopa.telnychat.server.commands.impl.GetTopicsChatCommand;
import it.pagopa.telnychat.server.commands.specs.ChatCommand;
import it.pagopa.telnychat.server.commons.Command;
import it.pagopa.telnychat.server.spec.ClientHandler;

import java.util.HashMap;
import java.util.Map;

public class ChatStrategy
{
    private Map<Command, ChatCommand> commands;

    public ChatStrategy()
    {
        commands = new HashMap<>();
        commands.put(Command.DISCONNECT,new DisconnectChatCommand());
        commands.put(Command.GET_CLIENTS,new GetClientsChatCommand());
        commands.put(Command.GET_TOPICS,new GetTopicsChatCommand());
        commands.put(Command.BROADCAST_TO_TOPIC,new BroadcastToTopicChatCommand());
    }

    public boolean execute(Command command,
                           ClientHandler handler,
                           String input)
    {
        boolean result = false;
        if(handler == null || command == null)
        {
            System.err.println("Client Handler and Command cannot be null!");
            return result;
        }
        // Execute Strategy
        return commands.get(command).execute(handler,input);
    }


}
