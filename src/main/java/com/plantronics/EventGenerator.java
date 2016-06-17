package com.plantronics;

import com.plantronics.impl.NearTalkWarningEventProfile;
import com.plantronics.impl.OverTalkWarningEventProfile;
import com.plantronics.impl.RandomEventProfile;
import com.plantronics.impl.RemoteTalkWarningEventProfile;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bthorington on 11/2/15.
 */
public class EventGenerator implements Runnable {

    EventPublisher eventPublisher;

    SoundEventProfile soundEventProfile = null;

    private String listenChannel = UUID.randomUUID().toString();
    private long timeBetweenEvents = 200;
    private boolean running = true;

    private Random random = new Random();

    private String version = "1.0";

    private String deviceId = "";
    private static final Logger log = LoggerFactory.getLogger(EventGenerator.class);
    private String[] deviceIdArr= new String[]{"12345524","12343212", "23124232", "32412323","12345526", "12345527", "12345528","12345529"};
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
                          int timeBetweenEvents, int channelCount) {
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
        this.channelCount=channelCount;
        channelMap= new ConcurrentHashMap<String, String>();
        for(int i=1; i<= 7;i++) {
            channelMap.put(deviceIdArr[i-1], "subdemo"+i);
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
                    sendSoundEvent();
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

        sb.append("\"type\":\"");
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
        deviceId=deviceIdArr[r.nextInt(High-Low) + Low];
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
        log.info("published to channel"+channelMap.get(deviceId));
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
