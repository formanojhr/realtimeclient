package com.plantronics;

import com.plantronics.impl.PubNubEventPublisher;
import com.plantronics.impl.TCPEventPublisher;
import com.plantronics.monitoring.internal.PerfLogger;
//import com.pubnub.api.Callback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubError;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
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

//            String publishKey = "demo";
//            String subscribeKey = "demo";

             String publishKey = "pub-c-f4cbb266-7f16-458d-8ee3-299e464010fd";
             String subscribeKey = "sub-c-4acf5b5c-0f40-11e7-85be-02ee2ddab7fe";
            PubNub pubNub;

            String sendChannel = "demo";
            String profile = "random";
            String listenChannel = UUID.randomUUID().toString();
            String deviceId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();
            //String tenantId = UUID.randomUUID().toString();
            String tenantId = "41a5d843-8a18-4bbd-9e77-351859c96a36";
            int timeBetweenEvents = 2000;
            int numClients = 1;

            String sendWith = "pubnub";
            String host = "54.186.207.24";
//            String host = "localhost";
            int port = 8088;
            int channelCount=4;
            boolean isDeviceEvent=false;
            boolean isSoundEvent=false;//default sound event
            boolean isEEG=true;


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
                        } else if ("-tenantId".equals(command)) {
                            tenantId = args[i];
                        }else if ("-isEEG".equals(command)) {
                            isEEG = Boolean.parseBoolean(args[i]);
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
                                            + "\n -tenantId: Id of the tenant the partner is subscribed to"
                            );
                        }
                    }
                    command = null;
                } else {
                    command = args[i];
                    if("-help".equals(command)) {
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
                        System.exit(0);
                    }
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
                    +"\n tenantId="+tenantId

            );



            if (sendWith.equalsIgnoreCase("pubnub")) {
                PNConfiguration pubnubConfig = new PNConfiguration();
                pubnubConfig.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
                pubnubConfig.setSecure(true);
                pubnubConfig.setSubscribeKey(subscribeKey);
                pubnubConfig.setPublishKey(publishKey);

                PubNub pubnub = new PubNub(pubnubConfig);


                perfLogger.getPerfLoggerInstance().gauge(METRIC_NAME_LATENCY_CLIENTS,numClients);
                for (int i=1; i<= numClients; i++) {


//                    pubnub.subscribe(listenChannel+"-"+i, new Callback() {
//                                @Override
//                                public void connectCallback(String channel, Object message) {
//                                    System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
//                                            + " : " + message.getClass() + " : "
//                                            + message.toString());
//                                }
//
//                                @Override
//                                public void disconnectCallback(String channel, Object message) {
//                                    System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
//                                            + " : " + message.getClass() + " : "
//                                            + message.toString());
//                                }
//
//                                public void reconnectCallback(String channel, Object message) {
//                                    System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
//                                            + " : " + message.getClass() + " : "
//                                            + message.toString());
//                                }
//
//                                @Override
//                                public void successCallback(String channel, Object message) {
//                                    System.out.println("SUBSCRIBE : " + channel + " : "
//                                            + message.getClass() + " : " + message.toString());
//                                }
//
//                                @Override
//                                public void errorCallback(String channel, PubnubError error) {
//                                    System.out.println("SUBSCRIBE : ERROR on channel " + channel
//                                            + " : " + error.toString());
//                                }
//                            }
//                    );
                    Thread.yield();
                }

                eventPublisher = new PubNubEventPublisher(pubnub,sendChannel);

            } else if (sendWith.equalsIgnoreCase("tcp")) {
                eventPublisher = new TCPEventPublisher(host, port);
            } else {
                throw new RuntimeException("The specified sendWith option isn't valid: " + sendWith);
            }

            eventGenerators = new ArrayList<EventGenerator>();
            perfLogger.getPerfLoggerInstance().gauge(METRIC_NAME_LATENCY_CLIENTS,numClients);
            log.info("tenantId" + tenantId);
            for (int i=1; i <= numClients; i++) {

                eventGenerators.add(
                        spinOffClient(eventPublisher,
                                profile,
                                listenChannel + "-" + i,
                                deviceId + "-" + i,
                                userId + "-" + i,
                                timeBetweenEvents, channelCount,isDeviceEvent,isSoundEvent,tenantId,isEEG)
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
            int timeBetweenEvents, int channelCount,boolean isDeviceEvent, boolean isSoundEvent, String tenantId,boolean isEEG)

    {

        EventGenerator eventGenerator = null;
        eventGenerator = new EventGenerator(eventPublisher, profile, listenChannel, deviceId, userId,
                timeBetweenEvents, channelCount,isDeviceEvent, isSoundEvent,tenantId, isEEG);

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
