package it.pagopa.telnychat.server.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Handle messages reading for specific connected Client
 */
public class ClientMsgConsumer extends Thread
{

    private Socket socket;          // Communication Channel
    private TelnyChatClient client; // Client instance
    private BufferedReader reader;  // Reader where client read server's messages

    public ClientMsgConsumer(Socket socket,
                             TelnyChatClient client)
    {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();               // Input on this Socket (where Server put message for this client)
            reader = new BufferedReader(new InputStreamReader(input)); // Reader for Input on this Socket
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true)
        {
            try
            {
                String response = reader.readLine();
                System.out.println(response);
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }

}
