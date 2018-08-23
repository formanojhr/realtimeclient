//package com.plantronics.impl;
//
//
//import com.plantronics.Constants;
//import com.plantronics.monitoring.internal.PerfLogger;
//import com.pubnub.api.Callback;
//import com.pubnub.api.Pubnub;
//import com.pubnub.api.PubnubError;
//import com.pubnub.api.PubnubException;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.TimeZone;
//import java.util.concurrent.*;
//
///**
// * Created by mramakrishnan on 6/9/16.
// */
//public class ResponseSubscriber {
//    private  BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>(
//            100);
//    private final ExecutorService executorService = new ThreadPoolExecutor(1, 10, 30,
//            TimeUnit.SECONDS, linkedBlockingDeque,
//            new ThreadPoolExecutor.CallerRunsPolicy());
//    private Pubnub _pubnub;
//    public static String METRIC_NAME_LATENCY="storm.load.test.latency";
//    private HashSet<String> channelSet;
//    private static final Logger log = LoggerFactory.getLogger(ResponseSubscriber.class);
//    private final PerfLogger perfLogger;
//
//    public ResponseSubscriber(final PerfLogger perfLogger, int channelCount) {
//        this.channelSet=new HashSet<String>();
//        this.perfLogger = perfLogger;
//        for (int i = 1; i <= channelCount; i++) {
//            String channelName = "pubdemo" + i;
//            log.info("Adding channel name: " + channelName);
//            channelSet.add(channelName);
//        }
//
//        _pubnub = new Pubnub(Constants.PUBNUB_PUB_KEY, Constants.PUBNUB_SUB_KEY, false);
//
//        for(String channel:channelSet) {
//            try {
//                log.info("Creating subscription with channel.. "+ channel);
//                _pubnub.subscribe(new String[]{channel}, new Callback() {
//                    @Override
//                    public void successCallback(String channel, Object message) {
//                        log.info("Successfully getting messages from channel:  " + channel);
//                        executorService.submit(new LatencyCalculator(message.toString(), log, perfLogger));
//                    }
//
//                    @Override
//                    public void errorCallback(String channel, PubnubError error) {
//                        log.error("Error getting a response from channel:" + channel + "with error" + error.getErrorString());
//                    }
//                });
//
//            } catch (PubnubException e) {
//                log.error("Pub Nub Exception:", e);
//            }
//        }
//    }
//
//    /**
//     * main for start a pub nub response subscriber
//     * @param args
//     */
//    public static void main(String[] args) {
//        PerfLogger perfLogger= new PerfLogger();
//        try {
//            perfLogger.init();
//        }
//        catch (Exception ex){
//            log.error("Error in statsd init: ", ex);
//        }
//        int channelCount=Integer.parseInt(args[0]);
//        ResponseSubscriber responseSubscriber = new ResponseSubscriber(perfLogger, channelCount);
//    }
//
//
//    public ResponseSubscriber(final PerfLogger perfLogger) {
//        _pubnub = new Pubnub(Constants.PUBNUB_PUB_KEY, Constants.PUBNUB_SUB_KEY, false);
//
//        try {
//            _pubnub.subscribe(new String[]{Constants.PUBNUB_PUB_CHANNEL}, new Callback() {
//                @Override
//                public void successCallback(String channel, Object message) {
//                    log.info("Successfully getting messages from channel:  " + channel);
//                        executorService.submit(new LatencyCalculator(message.toString(), log, perfLogger));
//                }
//
//                @Override
//                public void errorCallback(String channel, PubnubError error) {
//                    log.error("Error getting a response from channel:" + channel + "with error" + error.getErrorString());
//                }
//            });
//            log.info("PubNub subscription starting...in PUB CHANNEL :" + Constants.PUBNUB_PUB_CHANNEL);
//
//        } catch (PubnubException e) {
//            log.error("Pub Nub Exception:", e);
//        }
//
//        this.perfLogger = perfLogger;
//    }
//}
//
//class LatencyCalculator implements Runnable {
//    private String message;
//    private Logger log;
////    private  DateTimeFormatter df = ISODateTimeFormat.dateTime();
//    private PerfLogger perfLogger;
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
////    DateTimeFormatter patternFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
//    private TimeZone tz = TimeZone.getTimeZone("UTC");
//
//    private DateTime dt;
//
//    private DateTimeFormatter fmt;
//
//    public LatencyCalculator(String message, Logger log, PerfLogger perfLogger) {
//        this.message = message;
//        this.log=log;
//        df.setTimeZone(tz);
//
//        this.dt= new DateTime();
//        this.fmt= ISODateTimeFormat.dateTime();
//        this.perfLogger=perfLogger;
//    }
//
//    public void run() {
//        try {
//            JSONObject obj = new JSONObject(this.message);
//            log.debug("originTime:" + obj.get("originTime"));
//            Long timeElapsedWithOrigin = new Date().getTime() - Long.parseLong(obj.get("originTime").toString());
//            perfLogger.getPerfLoggerInstance().recordExecutionTime(ResponseSubscriber.METRIC_NAME_LATENCY, timeElapsedWithOrigin);
//            log.info("timeElapsedWithOrigin:" + timeElapsedWithOrigin);
//        } catch (Exception ex) {
//            log.error("Error deserializing : " ,ex);
//        }
//    }
//}
