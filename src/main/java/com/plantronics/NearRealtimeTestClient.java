package com.plantronics;

import com.plantronics.impl.PubNubEventPublisher;
import com.plantronics.impl.TCPEventPublisher;
import com.plantronics.monitoring.internal.PerfLogger;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NearRealtimeTestClient {
    private static final Logger log =  LoggerFactory.getLogger(NearRealtimeTestClient.class);
    public static String METRIC_NAME_LATENCY_CLIENTS="storm.load.test.clients(ms)";
    public static String METRIC_NAME_EVENT_TIME_PERIOD="storm.load.event.timeperiod(ms)";
    public static void main(String[] args) {

        EventPublisher eventPublisher = null;
        PerfLogger perfLogger = new PerfLogger();
        try {
            perfLogger.init();
        } catch (Exception e) {
           log.error("Exception initializing perf logger",e);
        }
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
            String host = "54.186.207.24";
//            String host = "localhost";
            int port = 8088;
            int channelCount=1;
            boolean isDeviceEvent=false;
            boolean isSoundEvent=true;//default sound event

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
                            perfLogger.getPerfLoggerInstance().gauge(METRIC_NAME_EVENT_TIME_PERIOD,timeBetweenEvents);
                        } else if ("-num".equals(command)) {
                            numClients = new Integer(args[i]);
                        } else if ("-host".equals(command)) {
                            host = args[i];
                        } else if ("-port".equals(command)) {
                            port = new Integer(args[i]);
                        } else if ("-sendWith".equals(command)) {
                            sendWith = args[i];
                        } else if ("-channelCount ".equals(command)) {
                            channelCount = new Integer(args[i]);
                        } else if ("-isDeviceEvent".equals(command)) {
                                isDeviceEvent = Boolean.parseBoolean(args[i]);
                        }else if ("-isSoundEvent".equals(command)) {
                            isSoundEvent = Boolean.parseBoolean(args[i]);
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
                                            + "\n -channelCount     :  number of pub nub channels to distribute load"
                                            + "\n -isDeviceEvent: Send Quick Disconnect device events"
                                            + "\n -isSoundEvent: Send Conversation Dynamic events"
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
                    +"\n channelCount="+channelCount
                    +"\n isDeviceEvent="+true

            );



            if (sendWith.equalsIgnoreCase("pubnub")) {
                Pubnub pubnub = new Pubnub(publishKey, subscribeKey);
                perfLogger.getPerfLoggerInstance().gauge(METRIC_NAME_LATENCY_CLIENTS,numClients);
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
            perfLogger.getPerfLoggerInstance().gauge(METRIC_NAME_LATENCY_CLIENTS,numClients);
            for (int i=1; i <= numClients; i++) {

                eventGenerators.add(
                        spinOffClient(eventPublisher,
                                profile,
                                listenChannel + "-" + i,
                                deviceId + "-" + i,
                                userId + "-" + i,
                                timeBetweenEvents, channelCount,isDeviceEvent,isSoundEvent)
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
            int timeBetweenEvents, int channelCount,boolean isDeviceEvent, boolean isSoundEvent) {

        EventGenerator eventGenerator = null;
        eventGenerator = new EventGenerator(eventPublisher, profile, listenChannel, deviceId, userId,
                timeBetweenEvents, channelCount,isDeviceEvent, isSoundEvent);

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
