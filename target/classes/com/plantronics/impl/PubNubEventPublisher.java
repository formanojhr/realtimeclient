package com.plantronics.impl;

import com.plantronics.EventPublisher;
import com.plantronics.PubNubUtils;
import com.plantronics.monitoring.internal.PerfLogger;
//import com.pubnub.api.Callback;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubError;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.timgroup.statsd.StatsDClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by bthorington on 1/12/16.
 */
public class PubNubEventPublisher implements EventPublisher {

    private static final Logger log =  LoggerFactory.getLogger(PubNubEventPublisher.class);
    private PubNub pubnub;
    private String sendChannel = "demo";
    private String subscribeChannel = "299fe95b-3bc5-4251-8afa-5e95e3271008_sub1";
    private StatsDClient perfLogger=PerfLogger.getPerfLoggerInstance();
    private static String METRIC_NAME_PUBLISH_COUNT="storm.load.test.publish.count";
//    Callback callback = new Callback() {
//        public void successCallback(String channel, Object response) {
//            System.out.print(".");
//        }
//        public void errorCallback(String channel, PubNubError error) {
//            System.out.println(error.toString());
//        }
//    };


    public PubNubEventPublisher(PubNub pubnub, String sendChannel) {
        this.pubnub = pubnub;
        this.sendChannel = sendChannel;
    }

//    public PubNubEventPublisher(String sendChannel) {
//
//        this.sendChannel = sendChannel;
//    }

    @Override
    public void publish(String message) throws Exception {

        JSONObject jsonObject = new JSONObject(message);
        //pubnub.publish(sendChannel, jsonObject, callback);
        Thread.yield();

    }

    @Override
    public void publish(String message,String channel) throws Exception {

        JSONObject jsonObject = new JSONObject(message);
        //pubnub.publish(sendChannel, jsonObject, callback);
        Thread.yield();

    }

    public void publish(ObjectNode message, String sendChannel) throws Exception {
//        perfLogger.count(METRIC_NAME_PUBLISH_COUNT,1);
        pubnub.publish().
                message(message).
                channel(sendChannel).
                shouldStore(true).
                async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            log.error("Pubnub publish failed  error {} error category {}",
                                     status.getErrorData().getThrowable().getCause(), status.getCategory().name());
                            log.error("Pub Nub Pub Key {}", pubnub.getConfiguration().getPublishKey());
                            log.error("Pub Nub Sub Key {}", pubnub.getConfiguration().getSubscribeKey());
                        } else {
                            System.out.println("Publish success! Time token: {}" + result.getTimetoken());
                            PubNubUtils.handleStatusMessages(pubnub, status);
                        }
                    }
                });
        JSONObject jsonObject = new JSONObject(message);
//        pubnub.publish(sendChannel, jsonObject, callback);
        Thread.yield();

    }






    public void subscribe(String sendChannel) throws Exception {
        perfLogger.count(METRIC_NAME_PUBLISH_COUNT,1);
        //JSONObject jsonObject = new JSONObject(message);
        //pubnub.subscribe(subscribeChannel,  callback);
        Thread.yield();

    }

    @Override
    public void destroy() {

    }
}
