package com.plantronics;

import com.plantronics.impl.PubNubEventPublisher;
import com.plantronics.impl.TCPEventPublisher;
import com.pubnub.api.*;
import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NearRealtimeTestClient {
    public static void main(String[] args) {

        EventPublisher eventPublisher = null;
        List<EventGenerator> eventGenerators = null;
        try {

            String publishKey = "demo";
            String subscribeKey = "demo";

            String sendChannel = "demo";
            String profile = "random";
            String listenChannel = UUID.randomUUID().toString();
            String deviceId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();
            int timeBetweenEvents = 200;
            int numClients = 1;

            String sendWith = "pubnub";
            String host = "localhost";
            int port = 9999;

            String command = null;
            for(int i=0;i<args.length;i++) {
                if (command != null && args[i] != null) {
                    if (args[i].startsWith("-")) {
                        System.out.println("- Is a reserved starting character, badly formatted command " + command + "=" + args[i]);
                    } else {
                        if ("-user".equals(command)) {
                            userId = args[i];
                        } else if ("-device".equals(command)) {
                            deviceId = args[i];
                        } else if ("-send".equals(command)) {
                            sendChannel = args[i];
                        } else if ("-listen".equals(command)) {
                            listenChannel = args[i];
                        } else if ("-pub".equals(command)) {
                            publishKey = args[i];
                        } else if ("-sub".equals(command)) {
                            subscribeKey = args[i];
                        } else if ("-profile".equals(command)) {
                            profile = args[i];
                        } else if ("-time".equals(command)) {
                            timeBetweenEvents = new Integer(args[i]);
                        } else if ("-num".equals(command)) {
                            numClients = new Integer(args[i]);
                        } else if ("-host".equals(command)) {
                            host = args[i];
                        } else if ("-port".equals(command)) {
                            port = new Integer(args[i]);
                        } else if ("-sendWith".equals(command)) {
                            sendWith = args[i];
                        } else if ("-help".equals(command)) {
                            System.out.println("HELP:\n"
                                            + "\n -help    : this menu"
                                            + "\n -user    : user id [Random UUID]"
                                            + "\n -device  : device id [Random UUID]"
                                            + "\n -send    : send channel [demo]"
                                            + "\n -listen  : listen channel [Random UUID]"
                                            + "\n -pub     : publish key "
                                            + "\n -sub     : subscribe key "
                                            + "\n -profile : profile [random]/over_talk/remote_talk/near_talk"
                                            + "\n -time    : time between events in ms. [200]"
                                            + "\n -num     : number of clients [1]"
                                            + "\n -host    : TCP/IP ONLY host [localhost]"
                                            + "\n -port     : TCP/IP ONLY port [9999]"
                                            + "\n -sendWith: send with pubnub/tcp [tcp]"
                            );
                        }
                    }
                    command = null;
                } else {
                    command = args[i];
                }

            }

            System.out.println("Running Near Real-time Test Client with the following settings:\n"
                            +"\n userId="+userId
                            +"\n deviceId="+deviceId+"-[1 to "+numClients+"]"
                            +"\n profile="+profile
                            +"\n sendChannel="+sendChannel
                            +"\n listenChannels="+listenChannel+"-[1 to "+numClients+"]"
                            +"\n timeBetweenEvents="+timeBetweenEvents
                            +"\n tcp/ip host="+host
                            +"\n tcp/ip port="+port
                            +"\n sendWith="+sendWith

            );



            if (sendWith.equalsIgnoreCase("pubnub")) {
                Pubnub pubnub = new Pubnub(publishKey, subscribeKey);

                for (int i=1; i<= numClients; i++) {
                    pubnub.subscribe(listenChannel+"-"+i, new Callback() {
                                @Override
                                public void connectCallback(String channel, Object message) {
                                    System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                            + " : " + message.getClass() + " : "
                                            + message.toString());
                                }

                                @Override
                                public void disconnectCallback(String channel, Object message) {
                                    System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                            + " : " + message.getClass() + " : "
                                            + message.toString());
                                }

                                public void reconnectCallback(String channel, Object message) {
                                    System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                            + " : " + message.getClass() + " : "
                                            + message.toString());
                                }

                                @Override
                                public void successCallback(String channel, Object message) {
                                    System.out.println("SUBSCRIBE : " + channel + " : "
                                            + message.getClass() + " : " + message.toString());
                                }

                                @Override
                                public void errorCallback(String channel, PubnubError error) {
                                    System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                            + " : " + error.toString());
                                }
                            }
                    );
                    Thread.yield();
                }

                eventPublisher = new PubNubEventPublisher(pubnub, sendChannel);
            } else if (sendWith.equalsIgnoreCase("tcp")) {
                eventPublisher = new TCPEventPublisher(host, port);
            } else {
                throw new RuntimeException("The specified sendWith option isn't valid: " + sendWith);
            }

            eventGenerators = new ArrayList<EventGenerator>();
            for (int i=1; i <= numClients; i++) {
                eventGenerators.add(
                        spinOffClient(eventPublisher,
                                profile,
                                listenChannel + "-" + i,
                                deviceId + "-" + i,
                                userId + "-" + i,
                                timeBetweenEvents)
                );
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (eventPublisher != null) {
                eventPublisher.destroy();
            }
        }

    }



    public static EventGenerator spinOffClient (
            EventPublisher eventPublisher,
            String profile,
            String listenChannel,
            String deviceId,
            String userId,
            int timeBetweenEvents) {

        EventGenerator eventGenerator = null;
        eventGenerator = new EventGenerator(eventPublisher, profile, listenChannel, deviceId, userId, timeBetweenEvents);
        Thread eventGeneratorThread = new Thread(eventGenerator);
        try {
            eventGeneratorThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            eventGenerator.setRunning(false);
        }

        return eventGenerator;

    }


}
