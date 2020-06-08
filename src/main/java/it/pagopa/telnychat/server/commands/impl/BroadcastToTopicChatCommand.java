package it.pagopa.telnychat.server.commands.impl;

import it.pagopa.telnychat.server.commands.specs.ChatCommand;
import it.pagopa.telnychat.server.impl.TelnyChatServer;
import it.pagopa.telnychat.server.spec.ClientHandler;

public class BroadcastToTopicChatCommand implements ChatCommand
{

    public static final String TOPIC_NAME_KEY = "TOPIC_NAME_KEY";
    public static final String MESSAGE_KEY = "MESSAGE_KEY";

    @Override
    public boolean execute(ClientHandler handler, String input)
    {
        // In the form: COMMAND|TOPIC|MESSAGE
        String[] elements = input.split("\\|");

        // 1. Short Form: SPLITTED_ARRAY_LENGTH == 1 --> Input contains only the message that have to be broadcasted to Default BROADCAST Topic
        if(elements.length == 1)
        {
            String message = "[" + handler.whois() + "] : " + elements[0];
            handler.getServer().sendMessageOnTopic(handler.whois(), TelnyChatServer.BROADCAST_TOPIC,message);
        }
        // 2. Complete Form: SPLITTED_ARRAY_LENGTH == 3 --> Input contains message in the format COMMAND|TOPIC|MESSAGE
        else if(elements.length == 3)
        {
            String topic = elements[1];
            String message = "[" + handler.whois() + "] : " + elements[2];
            handler.getServer().sendMessageOnTopic(handler.whois(), topic,message);
        }
        else
        {
            handler.forwardMessageToClient("Server","WARNING: Broadcast message to Topic Command was requested but message " +
                                                                  "doesn't respect the correct pattern: COMMAND|TOPIC|MESSAGE." +
                                                                  "If you want to broadcast message to all clients you can also use only MESSAGE format.");
        }

//        if(!params.containsKey(TOPIC_NAME_KEY)){
//            handler.forwardMessageToClient("Server","WARNING: Specify Topic's name to broadcast message on it");
//        }
//        String topic = (String) params.get(TOPIC_NAME_KEY);
//        if(!params.containsKey(MESSAGE_KEY)){
//            handler.forwardMessageToClient("Server","WARNING: Specify a message to broadcast it on Topic: " + topic);
//        }

        return true;
    }
}
