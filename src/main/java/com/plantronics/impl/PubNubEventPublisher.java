package com.plantronics.impl;

import com.plantronics.EventPublisher;
import com.plantronics.monitoring.internal.PerfLogger;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.timgroup.statsd.StatsDClient;
import org.json.JSONObject;

/**
 * Created by bthorington on 1/12/16.
 */
public class PubNubEventPublisher implements EventPublisher {

    private Pubnub pubnub;
    private String sendChannel = "demo";
    private StatsDClient perfLogger=PerfLogger.getPerfLoggerInstance();
    private static String METRIC_NAME_PUBLISH_COUNT="storm.load.test.publish.count";
    Callback callback = new Callback() {
        public void successCallback(String channel, Object response) {
            System.out.print(".");
        }
        public void errorCallback(String channel, PubnubError error) {
            System.out.println(error.toString());
        }
    };


    public PubNubEventPublisher(Pubnub pubnub, String sendChannel) {
        this.pubnub = pubnub;
        this.sendChannel = sendChannel;
    }

    @Override
    public void publish(String message) throws Exception {

        JSONObject jsonObject = new JSONObject(message);
        pubnub.publish(sendChannel, jsonObject, callback);
        Thread.yield();

    }

    public void publish(String message, String sendChannel) throws Exception {
        perfLogger.count(METRIC_NAME_PUBLISH_COUNT,1);
        JSONObject jsonObject = new JSONObject(message);
        pubnub.publish(sendChannel, jsonObject, callback);
        Thread.yield();

    }

    @Override
    public void destroy() {

    }
}
