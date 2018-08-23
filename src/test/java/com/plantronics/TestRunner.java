package com.plantronics;

/**
 * Created by cdikshit on 8/22/18.
 */

import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(QDPubNubSubscribeTest.class,MutePubNubSubscribeTest.class,DonOnOffPubNubSubscribeTest.class,CallEventPubNubSubscribeTest.class);

        for (Failure failure : result.getFailures()) {
            Assert.fail("There are some test case failures.");
            System.out.println(failure.toString());
         }
         System.exit(0);
    }
}