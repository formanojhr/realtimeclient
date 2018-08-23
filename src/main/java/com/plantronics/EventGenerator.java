package com.plantronics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plantronics.impl.*;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bthorington on 11/2/15.
 */
public class EventGenerator implements Runnable {

    EventPublisher eventPublisher;

    SoundEventProfile soundEventProfile = null;
    DeviceEventProfile deviceEventProfile;

    private String listenChannel = UUID.randomUUID().toString();
    private String subscribeChannel = "299fe95b-3bc5-4251-8afa-5e95e3271008_sub1";
    private String publishKey = "pub-c-f4cbb266-7f16-458d-8ee3-299e464010fd";
    private String subscribeKey = "sub-c-4acf5b5c-0f40-11e7-85be-02ee2ddab7fe";
    private long period = 200;
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
    //private String tenantId;
    private String channelPrefix;
   // private String tenantId+"_pub"=tenantId+"_pub";
    List<String> channelList = new ArrayList<String>();
    private LinkedBlockingQueue<String> queue;
    private AtomicBoolean receivedMessage = new AtomicBoolean(false);



//    private static String channelPrefix="dc560b50-9e20-41b9-a76b-d32ebfbdcd7a_pub";
   private static String tenantId="777aa081-16c7-4a59-9aa2-1a182965d86d";
  // private String eventype="QD";
   private String eventype="DON";
// private String eventype="MUTE";
//private String eventype="HEADSET";

    public EventGenerator(EventPublisher eventPublisher,
                          String profile,
                          String listenChannel,
                          String deviceId,
                          String userId,
                          int timeBetweenEvents,String tenantId) {
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
        this.period = timeBetweenEvents;

        this.userId = userId;
        this.deviceId = deviceId;
        this.numEvents = new Random().nextInt((1000 - 100) + 1) + 100; // range [100-1000];
        //this.tenantId = tenantId;
        df.setTimeZone(tz);
        this.dt= new DateTime();
        this.fmt= ISODateTimeFormat.dateTime();
        this.tenantId = tenantId;
        this.channelPrefix=channelPrefix;

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
                          int timeBetweenEvents, int    channelCount,boolean isDeviceEvent, boolean isSoundEvent, String tenantId) {
        this.eventPublisher = eventPublisher;
        this.isDeviceEvent=isDeviceEvent;
        channelPrefix = tenantId+"_pub";
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
            channelMap= new ConcurrentHashMap<String, String>();

            for(int i=1; i<= channelCount;i++) {
                channelMap.put(deviceIdArrOptions[i-1], channelPrefix+i);
                deviceIdArr.add(deviceIdArrOptions[i-1]);
                channelList.add(channelPrefix+i);
                log.info("Added device: channelName => "+ deviceIdArr.get(i-1) + " : "+ channelPrefix+i);
            }
        }

        //If device events
        if(isDeviceEvent){
            deviceEventProfile= new QuickDisconnectEventProfile();
        }

        this.listenChannel = listenChannel;
        this.period = timeBetweenEvents;

        this.userId = userId;
        this.deviceId = deviceId;
        this.numEvents = new Random().nextInt((1000 - 100) + 1) + 100; // range [100-1000];
        df.setTimeZone(tz);
        this.dt= new DateTime();
        this.fmt= ISODateTimeFormat.dateTime();
        this.channelCount=channelCount;
        this.tenantId = tenantId;
       // this.channelPrefix=channelPrefix;
        channelMap= new ConcurrentHashMap<String, String>();
        High=channelCount;
        for(int i=1; i<= channelCount;i++) {
            channelMap.put(deviceIdArrOptions[i-1], channelPrefix+i);
            deviceIdArr.add(deviceIdArrOptions[i-1]);
            log.info("Added device: channelName => "+ deviceIdArr.get(i-1) + " : "+ channelPrefix+i);
        }
    }

    public List<String> getChannelList(){
        return channelList;
    }

    @Override
    public void run() {

        try {
//            sendDeviceReg();
            while (running) {
//                sendCallStart();
                for (int i=0; i < numEvents; i++) {
                    try {
                        Thread.sleep(period);
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
        DeviceEvent deviceEvent = deviceEventProfile.generateDeviceEvent(period);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode position = mapper.createObjectNode();
        ObjectNode product = mapper.createObjectNode();

        product.put("headset", deviceId);
        product.put("base", "YYY");
        position.put("productCode", product);//added complex product
        position.put(Constants.JSONFieldNames.TIME_STAMP,fmt.print(dt));
        position.put("tenantId",tenantId);

//        StringBuilder sb = new StringBuilder();
//
//        sb.append("{");
//
//        sb.append("\"version\":\"");
//        sb.append(version);
//        sb.append("\",");

      //  sb.append("\"eventType\":\"");
        if(deviceEvent.isConnected()==true) {
            if(this.eventype.contains("QD")) {
                //sb.append(Constants.JSONFieldNames.QUICK_CONNECT);
                position.put("eventType",Constants.JSONFieldNames.QUICK_CONNECT);
            }
            else if(this.eventype.contains("MUTE")){
                //sb.append(Constants.JSONFieldNames.MUTE_ON);
                position.put("eventType",Constants.JSONFieldNames.MUTE_ON);
            }else if(this.eventype.contains("DON")){
                //sb.append(Constants.JSONFieldNames.DON_ON);
                position.put("eventType",Constants.JSONFieldNames.DON_ON);
            }else if(this.eventype.contains("HEADSET")){
                //sb.append(Constants.JSONFieldNames.USB_CONNECT);
                position.put("eventType",Constants.JSONFieldNames.USB_CONNECT);
            }
        }
        else{
            if(this.eventype.contains("QD")) {
                //sb.append(Constants.JSONFieldNames.QUICK_DISCONNECT);
                position.put("eventType",Constants.JSONFieldNames.QUICK_DISCONNECT);
            }
            else if(this.eventype.contains("MUTE")){
                //sb.append(Constants.JSONFieldNames.MUTE_OFF);
                position.put("eventType",Constants.JSONFieldNames.MUTE_OFF);
            }else if(this.eventype.contains("DON")){
                //sb.append(Constants.JSONFieldNames.DON_OFF);
                position.put("eventType",Constants.JSONFieldNames.DON_OFF);
            }else if(this.eventype.contains("HEADSET")){
                //sb.append(Constants.JSONFieldNames.USB_DISCONNECT);
                position.put("eventType",Constants.JSONFieldNames.USB_DISCONNECT);
            }
        }
//        sb.append("\",");
//        sb.append("\""+Constants.JSONFieldNames.TIME_STAMP+"\":");
//        sb.append("\"");
//        sb.append(fmt.print(dt));
//        sb.append("\"");
//        log.debug(Constants.JSONFieldNames.TIME_STAMP+fmt.print(dt));
//        sb.append(",");
//
//        sb.append("\""+Constants.JSONFieldNames.ORIGIN_TIME+"\":");
//        sb.append("\"");
//        sb.append(new Date().getTime());
//        sb.append("\"");
//        log.debug("originTime: "+new Date().getTime());
//        sb.append(",");
//
//        sb.append("\"tenantId\":\"");
//        sb.append(tenantId);
//        sb.append("\",");
//
//        sb.append("\"productCode\":");
//        sb.append("{");
//        sb.append("\"base\":");
//        sb.append("\"xxxx\"");
//        sb.append(",");
//        sb.append("\"headset\":");
//        sb.append("\"yyyy\"");
//        sb.append("}");
//        sb.append(",");
//        sb.append("\"deviceId\":\"");
//        sb.append(deviceId);
////        sb.append("\",");
//        sb.append("\"}");

        eventPublisher.publish(position, channelMap.get(deviceId));

        //eventPublisher.publish(sb.toString(), channelMap.get(deviceId));
        eventPublisher.subscribe(subscribeChannel);

        if(eventPublisher instanceof PubNubEventPublisher) {
            log.info("published to channel id: " + channelMap.get(deviceId));
        }

        TimeUnit.SECONDS.sleep(10);
      //subscribeEvents();
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

       // eventPublisher.publish(sb.toString(),channelMap.get(deviceId));
    }

    /**
     * sending sound event
     */
    private void sendSoundEvent() throws Exception {
        deviceId=deviceIdArr.get(r.nextInt(High-Low) + Low);
        System.out.println("deviceId: "+deviceId);
        DateTimeFormatter patternFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        SoundEvent soundEvent = soundEventProfile.generateSoundEvent(period);
        //create product complex json
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode position = mapper.createObjectNode();
        ObjectNode product = mapper.createObjectNode();

//        position.put("eventType",Constants.JSONFieldNames.CONVER_DYNAMIC_EVENT);
        product.put("headset", deviceId);
        product.put("base", "YYY");
        position.put("productCode", product);//added complex product
//        position.put("version",version);
//        position.put(Constants.JSONFieldNames.TIME_STAMP,fmt.print(dt));
//        position.put(Constants.JSONFieldNames.ORIGIN_TIME,new Date().getTime());
//        position.put("tenantId",tenantId);

//        position.put("eventType",Constants.JSONFieldNames.CALL_EVENT);
       position.put("eventType",Constants.JSONFieldNames.CONVER_DYNAMIC_EVENT);
//        position.put("rxLevelIn", "100");
//        position.put("periodicity", "120");
 //          position.put("overTalk","150");
//        position.put("txLevelOut","250");
//        position.put("farTalk","350");
//        position.put("nearTalk","450");
        position.put(Constants.JSONFieldNames.TIME_STAMP,fmt.print(dt));
        position.put("tenantId",tenantId);
       // position.put("deviceId",deviceId);

        //add CD events

  //      position.put();
        ObjectNode cdEventDetail = mapper.createObjectNode();
        cdEventDetail.put(Constants.JSONFieldNames.AVERAGE_HEALTH,150);
        cdEventDetail.put(Constants.JSONFieldNames.HEALTH_ALERT_THRESHOLD,100);
        cdEventDetail.put(Constants.JSONFieldNames.NEAR_TALK_DURATION,soundEvent.getNearEndDuration());
        cdEventDetail.put(Constants.JSONFieldNames.FAR_TALK_DURATION,soundEvent.getFarEndDuration());
        cdEventDetail.put(Constants.JSONFieldNames.DOUBLE_TALK_DURATION,soundEvent.getOverTalkDuration());
        cdEventDetail.put(Constants.JSONFieldNames.PERIOD,1000);
        cdEventDetail.put(Constants.JSONFieldNames.FAR_END,soundEvent.getFarEndMaxDb());
        cdEventDetail.put(Constants.JSONFieldNames.NEAR_END,soundEvent.getNearEndMaxDb());
        cdEventDetail.put(Constants.JSONFieldNames.RX_LEVEL_OUT,-86.031250);
        cdEventDetail.put(Constants.JSONFieldNames.RX_NOISE_IN,-116.03906250);
        cdEventDetail.put(Constants.JSONFieldNames.RX_NOISE_OUT, -128.0);
        cdEventDetail.put(Constants.JSONFieldNames.RX_PEAK_IN,-59.761718750);
        cdEventDetail.put(Constants.JSONFieldNames.RX_PEAK_OUT,-66.21093750);
        cdEventDetail.put(Constants.JSONFieldNames.RX_VOLUME,-3.0);
        cdEventDetail.put(Constants.JSONFieldNames.SIDE_TONE_VOLUME,-7.50);
        cdEventDetail.put(Constants.JSONFieldNames.TX_LEVEL_IN,-57.410156250);
        cdEventDetail.put(Constants.JSONFieldNames.TX_NOISE_IN,-82.480468750);
        cdEventDetail.put(Constants.JSONFieldNames.TX_NOISE_OUT,-82.496093750);
        cdEventDetail.put(Constants.JSONFieldNames.TX_PEAK_IN, -28.57031250);
        cdEventDetail.put(Constants.JSONFieldNames.TX_PEAK_OUT,-26.74218750);
        cdEventDetail.put(Constants.JSONFieldNames.TX_VOLUME,3.0);
        cdEventDetail.put(Constants.JSONFieldNames.CALL_ID,"123");


//        ObjectNode callEventDetail = mapper.createObjectNode();
//        callEventDetail.put(Constants.JSONFieldNames.RELATED_DEVICE_EVENT,"relateddeviceevent");
//        callEventDetail.put(Constants.JSONFieldNames.USER_ACTION,"softphoneUI");
//        callEventDetail.put(Constants.JSONFieldNames.LINE_TYPE,"voip");
//        callEventDetail.put(Constants.JSONFieldNames.PLUGIN_ID,"3100");
//        callEventDetail.put(Constants.JSONFieldNames.CALL_ID,"1049327484109903149");
//        callEventDetail.put(Constants.JSONFieldNames.DEVICE_ID,"12345");
//        callEventDetail.put(Constants.JSONFieldNames.SOURCE,"Cisco Jabber");
//        callEventDetail.put(Constants.JSONFieldNames.TYPE,"call");
//        callEventDetail.put(Constants.JSONFieldNames.EVENT_TIME,"2017-11-20T21:12:15.330636Z");
//        callEventDetail.put(Constants.JSONFieldNames.SESSION_ID,"a671281f-73f7-4d01-a461-555f61e33afe");
//        callEventDetail.put(Constants.JSONFieldNames.DURATION,79000);
//        callEventDetail.put(Constants.JSONFieldNames.SOFTPHONE_VERSION,"11.8.1.251552");
//        callEventDetail.put(Constants.JSONFieldNames.DIRECTION,"outgoing");
//        callEventDetail.put(Constants.JSONFieldNames.NAME,"ended");

//
//        cdEventDetail.put(Constants.JSONFieldNames.NEAR_TALK_DURATION,soundEvent.getNearEndDuration());
//        cdEventDetail.put(Constants.JSONFieldNames.FAR_TALK_DURATION,soundEvent.getFarEndDuration());
//        cdEventDetail.put(Constants.JSONFieldNames.DOUBLE_TALK_DURATION,soundEvent.getOverTalkDuration());
//        cdEventDetail.put(Constants.JSONFieldNames.PERIOD,1000);
//        cdEventDetail.put(Constants.JSONFieldNames.FAR_END,soundEvent.getFarEndMaxDb());
//        cdEventDetail.put(Constants.JSONFieldNames.NEAR_END,soundEvent.getNearEndMaxDb());
//        cdEventDetail.put(Constants.JSONFieldNames.RX_LEVEL_OUT,-86.031250);
//        cdEventDetail.put(Constants.JSONFieldNames.RX_NOISE_IN,-116.03906250);




        //add the cd detail event
       position.put(Constants.JSONFieldNames.CONVER_DYNAMIC_EVENT,cdEventDetail);


        //add the call event detail event
//        position.put(Constants.JSONFieldNames.CALL_EVENT,callEventDetail);

//        StringBuilder sb = new StringBuilder();
//
//        sb.append("{");
//
//        sb.append("\"version\":\"");
//        sb.append(version);
//        sb.append("\",");
//
//        sb.append("\"eventType\":\"");
//        sb.append(Constants.JSONFieldNames.CONVER_DYNAMIC_EVENT);
//        sb.append("\",");
//
////        sb.append("\"eventTime\":");
//        sb.append("\""+Constants.JSONFieldNames.TIME_STAMP+"\":");
//        sb.append("\"");
//        sb.append(fmt.print(dt));
//        sb.append("\"");
//        log.debug("TimestamP: "+fmt.print(dt));
//        sb.append(",");
//
//        sb.append("\""+Constants.JSONFieldNames.ORIGIN_TIME+"\":");
//        sb.append("\"");
//        sb.append(new Date().getTime());
//        sb.append("\"");
//        log.debug("OriginTime: "+new Date().getTime());
//        sb.append(",");
//        sb.append("\"productCode\":");
//        sb.append("{");
//        sb.append("\"base\":");
//        sb.append("\"xxxx\"");
//        sb.append(",");
//        sb.append("\"headset\":");
//        sb.append("\"yyyy\"");
//        sb.append("}");
//        sb.append(",");
//        sb.append("\"deviceId\":\"");
//        sb.append(deviceId);
//        sb.append("\",");
//        sb.append("\"tenantId\":\"");
//        sb.append(tenantId);
//        sb.append("\",");
//
//
//        sb.append("\""+Constants.JSONFieldNames.PERIOD+"\":");
//        sb.append(1000);
//        sb.append(",");
//
////        sb.append("\"farEndDuration\":");
//        sb.append("\""+Constants.JSONFieldNames.FAR_TALK_DURATION+"\":");
//        sb.append(soundEvent.getFarEndDuration());
//        sb.append(",");
//
////        sb.append("\"nearEndDuration\":");
//        sb.append("\""+Constants.JSONFieldNames.NEAR_TALK_DURATION+"\":");
//        sb.append(soundEvent.getNearEndDuration());
//        sb.append(",");
//
////        sb.append("\"overTalkDuration\":");
//        sb.append("\""+Constants.JSONFieldNames.DOUBLE_TALK_DURATION+"\":");
//        sb.append(soundEvent.getOverTalkDuration());
//        sb.append(",");
//
//        sb.append("\"noTalkDuration\":");
//        sb.append(soundEvent.getNoTalkDuration());
//        sb.append(",");
//
//        sb.append("\"farEndMaxDb\":");
//        sb.append(soundEvent.getFarEndMaxDb());
//        sb.append(",");
//
//        sb.append("\"nearEndMaxDb\":");
//        sb.append(soundEvent.getNearEndMaxDb());
//
//        sb.append("}");

        eventPublisher.publish(position, channelMap.get(deviceId));
        if(eventPublisher instanceof PubNubEventPublisher) {
            log.info("published to channel" + channelMap.get(deviceId));
        }
        TimeUnit.SECONDS.sleep(10);
        //subscribeEvents();
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

        //eventPublisher.publish(sb.toString());
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

        //eventPublisher.publish(sb.toString());
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

        //eventPublisher.publish(sb.toString());
    }

    public void subscribeEvents() {
        queue =  new LinkedBlockingQueue<String>(1000);
        PNConfiguration pubnubConfig = new PNConfiguration();
        pubnubConfig.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubnubConfig.setSecure(true);
        pubnubConfig.setSubscribeKey(subscribeKey);
        pubnubConfig.setPublishKey(publishKey);

        PubNub pubnub = new PubNub(pubnubConfig);
        SubscribeCallback pnCallback = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    log.info("Pub Nub Status: {}. Calling reconnect.... ", status.getCategory().name());
                    // internet got lost, do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // do some magic and call reconnect when ready
                    log.info("Pub Nub Status: {}. Calling reconnect.... ", status.getCategory().name());
                    pubnub.reconnect();
                }
                if (status.isError()) {
                    log.error("Pubnub subscription failed for tenant {} error {}", status.getErrorData().getInformation());
                    log.error("Category {}", status.getCategory().name());
                    log.error("Error", status);
                } else {
                    PubNubUtils.handleStatusMessages(pubnub, status);
                }

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                System.out.println ("Successfully getting messages from channel:  " + message.getChannel());
                receivedMessage.compareAndSet(false, true);
                //queue.offer(message.getMessage().toString());
                if(receivedMessage.get() == true){
                    System.out.println(message.getMessage());
                    String msg = message.getMessage().toString();
//                    validateMessage(msg);
//                    JSONObject messageObject = new JSONObject(msg);
//                    String eventType = messageObject.getString("eventType");
//                    String tenantId = messageObject.getString("tenantId");
//                    Assert.assertEquals(eventType,"isConnected");
//                    Assert.assertEquals(tenantId,"777aa081-16c7-4a59-9aa2-1a182965d86d");
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        };
        pubnub.addListener(pnCallback);
        List<String> channelList = getChannelList();
        List<String> subscribeChannelList = new ArrayList<String>();
        subscribeChannelList.add(subscribeChannel);
        pubnub.subscribe().channels(subscribeChannelList).execute();
        log.info("PubNub subscription starting for channels : ");
        for (String channelName : subscribeChannelList) {
            log.info("channel name: " + channelName + " ");
        }
    }

}
