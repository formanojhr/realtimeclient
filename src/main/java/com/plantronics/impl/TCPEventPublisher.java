package com.plantronics.impl;

import com.plantronics.EventPublisher;
import com.plantronics.monitoring.internal.PerfLogger;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by bthorington on 1/12/16.
 */
public class TCPEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(TCPEventPublisher.class);
    private static String METRIC_NAME_PUBLISH_COUNT="storm.load.test.tcp.publish.count";
    private StatsDClient perfLogger= PerfLogger.getPerfLoggerInstance();
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
    public void publish(String message, String channel) throws Exception {
        if (socket == null) throw  new Exception("no socket connected!");
        DataOutputStream os;
        os = new DataOutputStream(socket.getOutputStream());
        PrintWriter pw = new PrintWriter(os);
        pw.println(message);
        pw.flush();
        perfLogger.count(METRIC_NAME_PUBLISH_COUNT,1);
//        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        log.info("Message: "+ message);
//        outToServer.writeBytes(message+"\n");
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
