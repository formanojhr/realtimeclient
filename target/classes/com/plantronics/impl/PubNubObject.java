package com.plantronics.impl;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.plantronics.PubNubUtils;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * Created by cdikshit on 7/12/18.
 */
public class PubNubObject {
   private PNConfiguration pubnubConfig;
    private String publishKey = "pub-c-f4cbb266-7f16-458d-8ee3-299e464010fd";
    private String subscribeKey = "sub-c-4acf5b5c-0f40-11e7-85be-02ee2ddab7fe";
    private PubNub pubnub;

    public PubNub getPubnub() {

        PNConfiguration pubnubConfig = new PNConfiguration();
        pubnubConfig.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubnubConfig.setSecure(true);
        pubnubConfig.setSubscribeKey(subscribeKey);
        pubnubConfig.setPublishKey(publishKey);

        pubnub = new PubNub(pubnubConfig);
        return pubnub;
    }


}
