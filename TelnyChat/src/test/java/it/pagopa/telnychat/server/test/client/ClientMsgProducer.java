package it.pagopa.telnychat.server.test.client;

import it.pagopa.telnychat.server.commons.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Handle messages writing from Specific Connected Client
 */
public class ClientMsgProducer extends Thread
{
    private Socket socket;          // Communication Channel
    private TelnyChatClient client; // Client instance
    private PrintWriter writer;     // Printer where Client send messages to Server

    public ClientMsgProducer(Socket socket, TelnyChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        String message;
        do {
            Scanner keyboard = new Scanner(System.in);
            message = keyboard.nextLine();
            writer.println(message);
        }
        while (!message.equals(Command.DISCONNECT.getValue()));

        // User send "DISCONNECT" command
        disconnect();
    }

    public void disconnect()
    {
        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
