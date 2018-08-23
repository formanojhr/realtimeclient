package com.plantronics.impl;

import com.plantronics.DeviceEvent;
import com.plantronics.DeviceEventProfile;

import java.util.Random;

/**
 *  An event profile which will generate random boolean values to simulate disconnect reconnect
 * Created by mramakrishnan on 12/2/15.
 */
public class QuickDisconnectEventProfile implements DeviceEventProfile {

    private Random random = new Random();
    public static String EVENT_NAME="QD";

    @Override
    public DeviceEvent generateDeviceEvent(long timeBetweenEvents) throws Exception {
        DeviceEvent deviceEvent=new DeviceEvent();
        boolean quickDisconnect = random.nextBoolean(); // range [0-10]%
        deviceEvent.setConnected(quickDisconnect);
        return deviceEvent;
    }
}
