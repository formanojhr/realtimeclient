package com.plantronics;

import com.plantronics.impl.QuickDisconnectEventProfile;

/**
 * Created by bthorington on 12/2/15.
 */
public interface DeviceEventProfile {

    public DeviceEvent generateDeviceEvent(long timeBetweenEvents) throws Exception;

}
