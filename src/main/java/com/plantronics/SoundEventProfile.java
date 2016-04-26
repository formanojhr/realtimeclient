package com.plantronics;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

/**
 * Created by bthorington on 12/2/15.
 */
public interface SoundEventProfile {

    public SoundEvent generateSoundEvent(long timeBetweenEvents) throws Exception;

}
