package com.plantronics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bthorington on 1/12/16.
 */
public class TestServer {
    public static void main(String args[]) {

        try {
            ServerSocket server = new ServerSocket(9999);
            while (true) {
                Socket client = server.accept();
                MessageHandler handler = new MessageHandler(client);
                handler.start();
            }
        } catch (Exception e) {
            System.err.println("Exception caught:" + e);
        }
    }
}


class MessageHandler extends Thread {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    Socket client;

    MessageHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            while (true) {
                String message = reader.readLine();
                if (message != null) {
                    System.out.println("[" + sdf.format(new Date()) + "] Received: " + message);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception caught: client disconnected.");
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                ;
            }
        }
    }
}