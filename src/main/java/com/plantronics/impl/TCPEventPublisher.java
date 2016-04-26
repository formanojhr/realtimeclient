package com.plantronics.impl;

import com.plantronics.EventPublisher;

import java.io.*;
import java.net.Socket;

/**
 * Created by bthorington on 1/12/16.
 */
public class TCPEventPublisher implements EventPublisher {


    private Socket socket;

    public TCPEventPublisher(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(String message) throws Exception {
        if (socket == null) throw  new Exception("no socket connected!");
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        outToServer.writeBytes(message+"\n");
    }


    @Override
    public void destroy() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
