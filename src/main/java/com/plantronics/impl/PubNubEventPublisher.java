package com.plantronics.impl;

import com.plantronics.EventPublisher;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import org.json.JSONObject;

/**
 * Created by bthorington on 1/12/16.
 */
public class PubNubEventPublisher implements EventPublisher {

    private Pubnub pubnub;
    private String sendChannel = "demo";

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

    @Override
    public void destroy() {

    }
}
