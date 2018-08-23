package com.plantronics;

import com.pubnub.api.PubNub;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cdikshit on 6/21/18.
 */
public class PubNubUtils {
    private static final Logger log =  LoggerFactory.getLogger(PubNubUtils.class);
        public static void handleStatusMessages(PubNub pubNub, PNStatus status) {
            // the status object returned is always related to subscribe but could contain
            // information about subscribe, heartbeat, or errors
            // use the operationType to switch on different options
//        switch (status.getOperation()) {
//            // let's combine unsubscribe and subscribe handling for ease of use
//            case PNSubscribeOperation:
//                // note: subscribe statuses never have traditional
//                // errors, they just have categories to represent the
//                // different issues or successes that occur as part of subscribe
//                switch (status.getCategory()) {
//                    case PNConnectedCategory: {
//                        log.info("Successfully Connected PN Subscriptions.");
//                    }
//                    break;
//                    case PNReconnectedCategory:
//                        log.info("Pub nub client successfully reconnected. Error info {}",status.getErrorData().getInformation());
//                        break;
//                    case PNDisconnectedCategory:
//                        log.info("Pub nub client successfully unsubscribed.channels affected {}, status code{} error info {}", status.getErrorData().getInformation());
//                        break;
//                    case PNUnexpectedDisconnectCategory:
//                        log.error("Pub nub client disconnected. Check network connectivity!, status ", status);// internet got lost, do some magic and call reconnect when ready
//                        log.info("Pub nub client trying reconnect...");
//                        pubNub.reconnect();
//                        break;
//                    case PNTimeoutCategory:
//                        log.error("Pub nub client time out. Check network connectivity!, status ", status);// internet got lost, do some magic and call reconnect when ready
//                        log.info("Pub nub client trying reconnect...");
//                        pubNub.reconnect();
//                        break;
//                    case PNAccessDeniedCategory:
//                        log.error("Pub nub client was denied connection! channels affected{}, status code{} error info{}", status.getStatusCode(), status.getErrorData().getInformation());
//                        break;
//                    default:
//                        if (status.isError()) {
//                            // More errors can be directly specified by creating explicit cases for other
//                            // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
//                            log.error("Pub nub default status; channels affected{}, status code{} error info {}", status.getAffectedChannels(), status.getStatusCode(), status.getErrorData().getInformation());
//                        }
//                        else{
//                            log.info("Pub nub default status, status code {}",status.getStatusCode(),status.getErrorData());
//                        }
//                        break;
//                }
//            case PNUnsubscribeOperation:
//                // note: subscribe statuses never have traditional
//                // errors, they just have categories to represent the
//                // different issues or successes that occur as part of subscribe
//                switch (status.getCategory()) {
//                    case PNConnectedCategory:
//                        log.debug("Successfully getting messages.channels affected{}", status.getAffectedChannels());
//                        break;
//                    case PNReconnectedCategory:
//                        log.info("Pub nub client successfully reconnected.");
//                        break;
//                    case PNDisconnectedCategory:
//                        log.info("Pub nub client successfully unsubscribed.channels affected{}, status code{} error info{}");
//                        break;
//                    case PNUnexpectedDisconnectCategory:
//                        log.error("Pub nub client disconnected. Check network connectivity!channels affected{}, status code{} error info{}", status.getAffectedChannels(), status.getStatusCode(), status.getErrorData().getInformation());
//                        break;
//                    case PNAccessDeniedCategory:
//                        log.error("Pub nub client was denied connection! channels affected{}, status code{} error info{}", status.getAffectedChannels(), status.getStatusCode(), status.getErrorData().getInformation());
//                        break;
//                    default:
//                        if (status.isError()) {
//                            // More errors can be directly specified by creating explicit cases for other
//                            // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
//                            log.error("Pub nub default status; channels affected{}, status code{} error info {}", status.getAffectedChannels(), status.getStatusCode(), status.getErrorData().getInformation());
//                        }
//                        else{
//                            log.info("Pub nub default status, status code {}",status.getStatusCode(),status.getErrorData());
//                        }
//                        break;
//                }
//        }
            if(status != null) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    log.error("PNUnexpectedDisconnectCategory; Reconnecting....");
                    // internet got lost, do some magic and call reconnect when ready
                    pubNub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    log.error("PNUnexpectedDisconnectCategory; Reconnecting....");
                    // do some magic and call reconnect when ready
                    pubNub.reconnect();
                } else {
                    System.out.println("Category not found. Status:"+ status.getCategory() + " ErrorData  " + status.getErrorData());
                }
            }
        }
    }


