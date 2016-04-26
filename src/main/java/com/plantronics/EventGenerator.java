package com.plantronics;

import com.plantronics.impl.NearTalkWarningEventProfile;
import com.plantronics.impl.OverTalkWarningEventProfile;
import com.plantronics.impl.RandomEventProfile;
import com.plantronics.impl.RemoteTalkWarningEventProfile;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
    private String deviceType = "";
    private String locationName = "";
    private String locationId = "";
    private String userId = "";
    private String userName = "";
    private String company = "";
    private String managerChannel = "";
    private int numEvents = 100;


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

    }

    @Override
    public void run() {

        try {
            sendDeviceReg();
            while (running) {
                sendCallStart();
                for (int i=0; i < numEvents; i++) {
                    try {
                        Thread.sleep(timeBetweenEvents);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendSoundEvent();
                }
                sendCallEnd();
                int sleepTime = new Random().nextInt((2000 - 500) + 1) + 500;
                Thread.sleep(sleepTime);
            }
            sendDeviceDeReg();
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

        eventPublisher.publish(sb.toString());
    }

    /**
     * sending sound event
     */
    private void sendSoundEvent() throws Exception {


        SoundEvent soundEvent = soundEventProfile.generateSoundEvent(timeBetweenEvents);

        StringBuilder sb = new StringBuilder();

        sb.append("[{");

        sb.append("\"version\":\"");
        sb.append(version);
        sb.append("\",");

        sb.append("\"type\":\"");
        sb.append("caReport");
        sb.append("\",");

        sb.append("\"eventTime\":");
        sb.append(new Date().getTime());
        sb.append(",");

        sb.append("\"deviceId\":\"");
        sb.append(deviceId);
        sb.append("\",");

        sb.append("\"timePeriod\":");
        sb.append(timeBetweenEvents);
        sb.append(",");

        sb.append("\"farEndDuration\":");
        sb.append(soundEvent.getFarEndDuration());
        sb.append(",");

        sb.append("\"nearEndDuration\":");
        sb.append(soundEvent.getNearEndDuration());
        sb.append(",");

        sb.append("\"overTalkDuration\":");
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

        sb.append("}]");

        eventPublisher.publish(sb.toString());
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
