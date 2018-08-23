package com.plantronics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plantronics.impl.PubNubEventPublisher;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.Properties;

/**
 * Created by cdikshit on 6/11/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class MutePubNubSubscribeTest {
    private static final Logger log = LoggerFactory.getLogger(MutePubNubSubscribeTest.class);
    private boolean receivedMessage = false;
    private boolean verifiedMsg = false;
    private String subscribeChannel = "299fe95b-3bc5-4251-8afa-5e95e3271008_sub1";
    private String publishKey = "pub-c-f4cbb266-7f16-458d-8ee3-299e464010fd";
    private String subscribeKey = "sub-c-4acf5b5c-0f40-11e7-85be-02ee2ddab7fe";
    private PubNub pubnub;
    private DateTimeFormatter fmt;
    private DateTime dt;
    private EventPublisher eventPublisher;
    private ConcurrentHashMap<String, String> channelMap;
    protected Properties appProperties;
    private static final String PROPERTY_FILE="application.properties";
    private String tenantId;
    private String tenantPublishChannel;

    @Before
    public void init()  throws Exception {
        System.out.println("Inside init");
        appProperties = new Properties();
        try {
            appProperties.load(ClassLoader.getSystemResourceAsStream(PROPERTY_FILE));
        } catch (FileNotFoundException e) {
            log.error("Encountered FileNotFoundException while reading configuration properties: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Encountered IOException while reading configuration properties: " + e.getMessage());
            throw e;
        }
        subscribeKey = appProperties.getProperty("sub.key");
        publishKey = appProperties.getProperty("pub.key");
        subscribeChannel= appProperties.getProperty("app.subscribe.channel");
        tenantId = appProperties.getProperty("tenantId");
        tenantPublishChannel = appProperties.getProperty("tenant.publish.channel");
        MockitoAnnotations.initMocks(this);
        PNConfiguration pubnubConfig = new PNConfiguration();
        pubnubConfig.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubnubConfig.setSecure(true);
        pubnubConfig.setSubscribeKey(subscribeKey);
        pubnubConfig.setPublishKey(publishKey);

        pubnub = new PubNub(pubnubConfig);
        dt = new DateTime();
        fmt = ISODateTimeFormat.dateTime();

        eventPublisher = new PubNubEventPublisher(pubnub, subscribeChannel);
        channelMap = new ConcurrentHashMap<String, String>();

    }

    private void publishMsg() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode position = mapper.createObjectNode();
        ObjectNode product = mapper.createObjectNode();

        product.put("headset", "123");
        product.put("base", "YYY");
        position.put("productCode", product);//added complex product
        position.put(Constants.JSONFieldNames.TIME_STAMP, fmt.print(dt));
        position.put("tenantId", tenantId);
        position.put("eventType", Constants.JSONFieldNames.MUTE_ON);
        try {
            eventPublisher.publish(position, tenantPublishChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPubSub() throws Exception {
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
                receivedMessage= true;
                //queue.offer(message.getMessage().toString());
                if(receivedMessage == true){
                    System.out.println(message.getMessage());
                    String msg = message.getMessage().toString();
                    //validateMessage(msg);
                    JSONObject object = new JSONObject(msg);
                    String eventType = object.getString("eventType");
                    System.out.println("Event Type is " + eventType);
                    Assert.assertEquals(eventType,"isMute");
                    if(eventType.equals("isMute")) {
                        verifiedMsg = true;
                    }
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        };

        pubnub.addListener(pnCallback);
        List<String> subscribeChannelList = new ArrayList<String>();
        subscribeChannelList.add(subscribeChannel);
        pubnub.subscribe().channels(subscribeChannelList).execute();
        log.info("PubNub subscription starting for channels : ");
        for (String channelName : subscribeChannelList) {
            log.info("channel name: " + channelName + " ");
        }

        publishMsg();
        TimeUnit.SECONDS.sleep(5);
        if(!(receivedMessage && verifiedMsg)){
            Assert.fail("Either msg was not received or was incorrect");
        }  else {
            log.info("message was received and verified! Test Passed !!");
        }
        pubnub.stop();

    }
//
//    public void validateMessage(String msg){
//
//        JSONObject messageObject = new JSONObject(msg);
//        String eventType = messageObject.getString("eventType");
//        Assert.assertEquals(eventType,"QD");
//    }

}

