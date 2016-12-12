package com.plantronics;

import com.plantronics.impl.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bthorington on 11/2/15.
 */
public class EventGenerator implements Runnable {

    EventPublisher eventPublisher;

    SoundEventProfile soundEventProfile = null;
    DeviceEventProfile deviceEventProfile;

    private String listenChannel = UUID.randomUUID().toString();
    private long timeBetweenEvents = 200;
    private boolean running = true;

    private Random random = new Random();

    private String version = "1.0";

    private String deviceId = "";
    private static final Logger log = LoggerFactory.getLogger(EventGenerator.class);
    private String[] deviceIdArrOptions= new String[]{"12345524","12343212", "23124232", "32412323","12345526", "12345527", "12345528","12345529"};
    private ArrayList<String> deviceIdArr= new ArrayList<String>();
    private ConcurrentHashMap<String, String> channelMap;
    private String deviceType = "";
    private String locationName = "";
    private String locationId = "";
    private String userId = "";
    private String userName = "";
    private String company = "";
    private String managerChannel = "";
    private int numEvents = 100;
    private DateTime dt;
    private DateTimeFormatter fmt;
    Random r = new Random();
    private int Low = 0;
    private int High = 7;
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private int channelCount;
    private boolean isDeviceEvent;
    private boolean isSoundEvent;
    private static String channelPrefix="7f0bc41d-be73-4ae2-89f0-06a128c7902d_pub";
//    private static String channelPrefix="dc560b50-9e20-41b9-a76b-d32ebfbdcd7a_pub";
    private static String tenantId="7f0bc41d-be73-4ae2-89f0-06a128c7902d";
    private String eventype="QD";
//    private String eventype="MUTE";
    public EventGenerator(EventPublisher eventPublisher,
                          String profile,
                          String listenChannel,
                          String deviceId,
                          String userId,
                          int timeBetweenEvents) {
        this.eventPublisher = eventPublisher;
        if ("over_talk".equals(profile)) {
            soundEventProfile = new OverTalkWarningEventProfile();
        } else if ("remote_talk".equals(profile)) {
            soundEventProfile = new RemoteTalkWarningEventProfile();
        } else if ("near_talk".equals(profile)) {
            soundEventProfile = new NearTalkWarningEventProfile();
        } else { // random...
            soundEventProfile = new RandomEventProfile();
        }

        this.listenChannel = listenChannel;
        this.timeBetweenEvents = timeBetweenEvents;

        this.userId = userId;
        this.deviceId = deviceId;
        this.numEvents = new Random().nextInt((1000 - 100) + 1) + 100; // range [100-1000];
        df.setTimeZone(tz);
        this.dt= new DateTime();
        this.fmt= ISODateTimeFormat.dateTime();
        channelMap= new ConcurrentHashMap<String, String>();
        int i=1;
        for(String device:deviceIdArr) {
            channelMap.put(device, "subdemo"+i);
            i++;
        }
    }

    public EventGenerator(EventPublisher eventPublisher,
                          String profile,
                          String listenChannel,
                          String deviceId,
                          String userId,
                          int timeBetweenEvents, int channelCount,boolean isDeviceEvent, boolean isSoundEvent) {
        this.eventPublisher = eventPublisher;
        this.isDeviceEvent=isDeviceEvent;
        //Sound events
        if(isSoundEvent) {
            this.isSoundEvent = isSoundEvent;
            if ("over_talk".equals(profile)) {
                soundEventProfile = new OverTalkWarningEventProfile();
            } else if ("remote_talk".equals(profile)) {
                soundEventProfile = new RemoteTalkWarningEventProfile();
            } else if ("near_talk".equals(profile)) {
                soundEventProfile = new NearTalkWarningEventProfile();
            } else { // random...
                soundEventProfile = new RandomEventProfile();
            }
            for(int i=1; i<= channelCount;i++) {
                channelMap.put(deviceIdArrOptions[i-1], "subdemo"+i);
                deviceIdArr.add(deviceIdArrOptions[i-1]);
                log.info("Added device: channelName => "+ deviceIdArr.get(i-1) + " : "+ "subdemo"+i);
            }
        }
        //If device events
        if(isDeviceEvent){
            deviceEventProfile= new QuickDisconnectEventProfile();
        }

        this.listenChannel = listenChannel;
        this.timeBetweenEvents = timeBetweenEvents;

        this.userId = userId;
        this.deviceId = deviceId;
        this.numEvents = new Random().nextInt((1000 - 100) + 1) + 100; // range [100-1000];
        df.setTimeZone(tz);
        this.dt= new DateTime();
        this.fmt= ISODateTimeFormat.dateTime();
        this.channelCount=channelCount;
        channelMap= new ConcurrentHashMap<String, String>();
        High=channelCount;
        for(int i=1; i<= channelCount;i++) {
            channelMap.put(deviceIdArrOptions[i-1], channelPrefix+i);
            deviceIdArr.add(deviceIdArrOptions[i-1]);
            log.info("Added device: channelName => "+ deviceIdArr.get(i-1) + " : "+ channelPrefix+i);
        }
    }

    @Override
    public void run() {

        try {
//            sendDeviceReg();
            while (running) {
//                sendCallStart();
                for (int i=0; i < numEvents; i++) {
                    try {
                        Thread.sleep(timeBetweenEvents);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(isDeviceEvent){
                        sendDeviceEvent();
                    }
                    if(isSoundEvent) {
                        sendSoundEvent();
                    }
                }
//                sendCallEnd();
                int sleepTime = new Random().nextInt((2000 - 500) + 1) + 500;
                Thread.sleep(sleepTime);
            }
//            sendDeviceDeReg();
        } catch (Exception e) {
            running = false;
            e.printStackTrace();
        }
    }

    private void sendDeviceEvent() throws Exception{
        deviceId=deviceIdArr.get(r.nextInt(High-Low) + Low);
        System.out.println("deviceId: "+deviceId);
        DateTimeFormatter patternFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        DeviceEvent deviceEvent = deviceEventProfile.generateDeviceEvent(timeBetweenEvents);

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"eventType\":\"");
        if(deviceEvent.isConnected()==true) {
            if(this.eventype.contains("QD")) {
                sb.append(Constants.JSONFieldNames.QUICK_CONNECT);
            }
            else if(this.eventype.contains("MUTE")){
                sb.append(Constants.JSONFieldNames.MUTE_ON);
            }
        }
        else{
            if(this.eventype.contains("QD")) {
                sb.append(Constants.JSONFieldNames.QUICK_DISCONNECT);
            }
            else if(this.eventype.contains("MUTE")){
                sb.append(Constants.JSONFieldNames.MUTE_OFF);
            }
        }
        sb.append("\",");
        sb.append("\""+Constants.JSONFieldNames.TIME_STAMP+"\":");
        sb.append("\"");
        sb.append(fmt.print(dt));
        sb.append("\"");
        log.debug(Constants.JSONFieldNames.TIME_STAMP+fmt.print(dt));
        sb.append(",");

        sb.append("\""+Constants.JSONFieldNames.ORIGIN_TIME+"\":");
        sb.append("\"");
        sb.append(new Date().getTime());
        sb.append("\"");
        log.debug("originTime: "+new Date().getTime());
        sb.append(",");

        sb.append("\"tenantId\":\"");
        sb.append(tenantId);
        sb.append("\",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\",");
        sb.append("}");



        eventPublisher.publish(sb.toString(), channelMap.get(deviceId));
        if(eventPublisher instanceof PubNubEventPublisher) {
            log.info("published to channel id: " + channelMap.get(deviceId));
        }
    }

    public void setRunning(boolean value) {
        this.running = value;
    }

    /**
     * sending the device register
     */
    private void sendDeviceReg() throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"eventType\":\"");
        sb.append("rtRegister");
        sb.append("\",");

        sb.append("\"userId\":\"");
        sb.append(userId);
        sb.append("\",");

        sb.append("\"userName\":\"");
        sb.append(userName);
        sb.append("\",");

        sb.append("\"company\":\"");
        sb.append(company);
        sb.append("\",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\",");

        sb.append("\"deviceType\":\"");
        sb.append(deviceType);
        sb.append("\",");

        sb.append("\"listenChannel\":\"");
        sb.append(listenChannel);
        sb.append("\",");

        sb.append("\"managerChannel\":\"");
        sb.append(managerChannel);
        sb.append("\",");

        sb.append("\"managerChannel\":\"");
        sb.append(managerChannel);
        sb.append("\",");

        sb.append("\"locationId\":\"");
        sb.append(locationId);
        sb.append("\",");

        sb.append("\"locationName\":\"");
        sb.append(locationName);
        sb.append("\"");

        sb.append("}");

        eventPublisher.publish(sb.toString(),channelMap.get(deviceId));
    }

    /**
     * sending sound event
     */
    private void sendSoundEvent() throws Exception {
        deviceId=deviceIdArr.get(r.nextInt(High-Low) + Low);
        System.out.println("deviceId: "+deviceId);
        DateTimeFormatter patternFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        SoundEvent soundEvent = soundEventProfile.generateSoundEvent(timeBetweenEvents);

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"type\":\"");
        sb.append("conversationDynamic");
        sb.append("\",");

//        sb.append("\"eventTime\":");
        sb.append("\""+Constants.JSONFieldNames.TIME_STAMP+"\":");
        sb.append("\"");
        sb.append(fmt.print(dt));
        sb.append("\"");
        log.debug("TimestamP: "+fmt.print(dt));
        sb.append(",");

        sb.append("\""+Constants.JSONFieldNames.ORIGIN_TIME+"\":");
        sb.append("\"");
        sb.append(new Date().getTime());
        sb.append("\"");
        log.debug("OriginTime: "+new Date().getTime());
        sb.append(",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\",");

        sb.append("\"timePeriod\":");
        sb.append(timeBetweenEvents);
        sb.append(",");

//        sb.append("\"farEndDuration\":");
        sb.append("\""+Constants.JSONFieldNames.FAR_TALK_DURATION+"\":");
        sb.append(soundEvent.getFarEndDuration());
        sb.append(",");

//        sb.append("\"nearEndDuration\":");
        sb.append("\""+Constants.JSONFieldNames.NEAR_TALK_DURATION+"\":");
        sb.append(soundEvent.getNearEndDuration());
        sb.append(",");

//        sb.append("\"overTalkDuration\":");
        sb.append("\""+Constants.JSONFieldNames.DOUBLE_TALK_DURATION+"\":");
        sb.append(soundEvent.getOverTalkDuration());
        sb.append(",");

        sb.append("\"noTalkDuration\":");
        sb.append(soundEvent.getNoTalkDuration());
        sb.append(",");

        sb.append("\"farEndMaxDb\":");
        sb.append(soundEvent.getFarEndMaxDb());
        sb.append(",");

        sb.append("\"nearEndMaxDb\":");
        sb.append(soundEvent.getNearEndMaxDb());

        sb.append("}");

        eventPublisher.publish(sb.toString(), channelMap.get(deviceId));
        if(eventPublisher instanceof PubNubEventPublisher) {
            log.info("published to channel" + channelMap.get(deviceId));
        }
    }

    /**
     * sending device de-register.
     */
    private void sendDeviceDeReg() throws Exception{

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"type\":\"");
        sb.append("rtDeRegister");
        sb.append("\",");

        sb.append("\"userId\":\"");
        sb.append(userId);
        sb.append("\",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\"");

        sb.append("}");

        eventPublisher.publish(sb.toString());
    }

    /**
     * sending call start.
     */
    private void sendCallStart() throws Exception{

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"type\":\"");
        sb.append("rtCallStart");
        sb.append("\",");

        sb.append("\"userId\":\"");
        sb.append(userId);
        sb.append("\",");

        sb.append("\"timestamp\":\"");
        sb.append(new Date().getTime());
        sb.append("\",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\"");

        sb.append("}");

        eventPublisher.publish(sb.toString());
    }

    /**
     * sending call start.
     */
    private void sendCallEnd() throws Exception{

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"type\":\"");
        sb.append("rtCallEnd");
        sb.append("\",");

        sb.append("\"userId\":\"");
        sb.append(userId);
        sb.append("\",");

        sb.append("\"timestamp\":\"");
        sb.append(new Date().getTime());
        sb.append("\",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\"");

        sb.append("}");

        eventPublisher.publish(sb.toString());
    }


}
