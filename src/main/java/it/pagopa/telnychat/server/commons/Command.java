package it.pagopa.telnychat.server.commons;

import java.util.Arrays;
import java.util.Optional;

public enum Command
{
    DISCONNECT("___DISCONNECT"),
    GET_TOPICS("___GET_TOPICS"),
    GET_CLIENTS("___GET_CLIENTS"),
    BROADCAST_TO_TOPIC("___BROADCAST_TO_TOPIC");

    private String value;

    private Command(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<Command> getENUM(String value)
    {
        Optional<Command> result = Arrays.stream(Command.values()).filter(item -> item.value.equalsIgnoreCase(value)).findFirst();
        return result;
    }
}
