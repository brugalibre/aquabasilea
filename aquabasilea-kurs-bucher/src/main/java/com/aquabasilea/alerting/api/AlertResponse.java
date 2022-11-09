package com.aquabasilea.alerting.api;

/**
 * The {@link AlertResponse} is the basic response to any send request from a {@link AlertSendService}
 */
public interface AlertResponse {

   /**
    * @return The http-status of the response
    */
   int getStatus();

   /**
    * @return The response body
    */
   Object getResponseEntity();
}
