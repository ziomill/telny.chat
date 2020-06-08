package it.pagopa.telnychat.server.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TelnyChatClient
{
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public TelnyChatClient(Socket socket) throws IOException
    {
        writer = new PrintWriter(socket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;
    }

    public void sendMessage(String message)
    {
        writer.println(message);
    }

    public String getMessage() throws IOException
    {
        return reader.readLine();
    }

    public Socket getSocket() {
        return socket;
    }
}
