package com.plantronics;

/**
 * Created by mramakrishnan on 5/3/16.
 */
public class Constants {
    public static String PUBNUB_PUB_KEY="pub-c-d1314104-910c-46c1-9e58-32d7504b9a01";
    public static String PUBNUB_SUB_KEY="sub-c-cf5b662a-0da9-11e6-996b-0619f8945a4f";
    public static String PUBNUB_SUB_CHANNEL="demo";
    public static String PUBNUB_PUB_CHANNEL="demopub";
    public static final String timePeriodInMS = "1000";
    public static final class JSONFieldNames {
        public static final String NEAR_TALK_DURATION = "nearTalk";
        public static final String FAR_TALK_DURATION = "farTalk";
        public static final String DOUBLE_TALK_DURATION = "overTalk";
        public static final String FAR_END = "rxLevelIn";
        public static final String NEAR_END="txLevelOut";
        //public static final String AVERAGE_HEALTH = "health";
        public static final String HEALTH_ALERT_THRESHOLD="alertThreshold";
        public static final String TIME_STAMP="timeStamp";
        public static final String ORIGIN_TIME="originTime";
        public static final String IS_CONNECTED="isConnected";
        public static final String PERIOD="periodicity";
//        public static final String QUICK_DISCONNECT="quickDisconnect";
//        public static final String QUICK_CONNECT="quickConnect";
        public static final String QUICK_DISCONNECT="HS_STATE_CHANGE_QD_DISCONNECTED";
        public static final String QUICK_CONNECT="HS_STATE_CHANGE_QD_CONNECTED";
        public static final String RX_LEVEL_OUT = "rxLevelOut" ;
        public static final String RX_NOISE_IN = "rxNoiseIn";
        public static final String RX_NOISE_OUT = "rxNoiseOut";
        public static final String RX_PEAK_IN = "rxPeakIn";
        public static final String RX_PEAK_OUT = "rxPeakOut";
        public static final String RX_VOLUME = "rxVolume";
        public static final String SIDE_TONE_VOLUME = "sideToneVolume";
        public static final String TX_LEVEL_IN = "txLevelIn";
        public static final String TX_NOISE_IN = "txNoiseIn";
        public static final String TX_NOISE_OUT = "txNoiseOut";
        public static final String TX_PEAK_IN = "txPeakIn";
        public static final String TX_PEAK_OUT = "txPeakOut";
        public static final String TX_VOLUME = "txVolume";
        //public static final String CALL_ID = "callId";


        public static final String RELATED_DEVICE_EVENT = "relatedDeviceEvent";
        public static final String USER_ACTION = "userAction";
        public static final String LINE_TYPE = "lineType";
        public static final String PLUGIN_ID = "pluginId";
        public static final String CALL_ID = "callId";
        public static final String DEVICE_ID = "deviceId";
        public static final String SOURCE = "source";
        public static final String TYPE = "type";
        public static final String EVENT_TIME = "eventTime";
        public static final String SESSION_ID = "sessionId";
        public static final String DURATION = "duration";
        public static final String SOFTPHONE_VERSION = "softphoneVersion";
        public static final String DIRECTION = "direction";
        public static final String NAME = "name";


        public static final String MUTE_ON="HS_STATE_CHANGE_MUTE_ON";
        public static final String DON_ON="HS_STATE_CHANGE_DON";
        public static final String DON_OFF="HS_STATE_CHANGE_DOFF";
        public static final String MUTE_OFF="HS_STATE_CHANGE_MUTE_OFF";
        public static final String CONVER_DYNAMIC_EVENT="CD_EVENT";
        public static final String CALL_EVENT="CALL_EVENT";
        public static final String AVERAGE_HEALTH = "convHealth";
        public static final String BAD_CONVERSATION_EVENT = "badConv";
        public static final String HEADSET_BUTTON_TALK="HEADSET_BUTTON_TALK";
        public static final String HEADSET_BUTTON_REFECT="HEADSET_BUTTON_REJECT";
        public static final String USB_CONNECT="USB_CONNECT";
        public static final String USB_DISCONNECT="USB_DISCONNECT";
    }

}
