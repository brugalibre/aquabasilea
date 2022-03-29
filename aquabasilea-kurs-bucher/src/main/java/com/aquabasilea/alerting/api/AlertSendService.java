package com.aquabasilea.alerting.api;

import com.aquabasilea.alerting.config.AlertSendConfig;

/**
 * Common api for sending alerts
 */
public interface AlertSendService {

   /**
    * Sends an alert with the given configuration (contains receivers, api key and so on) and the given message
    *
    * @param alertSendConfig the {@link AlertSendConfig}
    * @param msg            the message to send
    * @return a response from the actual sms api service
    */
   String sendAlert(AlertSendConfig alertSendConfig, String msg) throws AlertSendException;
}
