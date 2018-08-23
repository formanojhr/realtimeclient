package com.plantronics;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by bthorington on 1/12/16.
 */
public interface EventPublisher {


   public void publish(String message)  throws Exception ;

    public void publish(ObjectNode message,String channel)  throws Exception ;

    public void publish(String message, String channel) throws Exception ;

    public void subscribe(String channel) throws Exception;

    public void destroy();

}
