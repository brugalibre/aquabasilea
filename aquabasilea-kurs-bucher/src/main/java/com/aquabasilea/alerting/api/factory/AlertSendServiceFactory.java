package com.aquabasilea.alerting.api.factory;

import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.sms.clicksend.ClickSendAlertSendServiceImpl;
import com.aquabasilea.alerting.sms.messagebird.MessageBirdAlertSendServiceImpl;

import static com.aquabasilea.alerting.constants.AlertConstants.CLICK_SEND_SMS_SERVICE_NAME;
import static com.aquabasilea.alerting.constants.AlertConstants.MESSAGE_BIRD_SMS_SERVICE_NAME;

/**
 * Common api for sending alerts
 */
public class AlertSendServiceFactory {

   public static AlertSendService getAlertSendService4Name(String alertServiceName) {
      if (CLICK_SEND_SMS_SERVICE_NAME.equals(alertServiceName)) {
         return new ClickSendAlertSendServiceImpl();
      } else if (MESSAGE_BIRD_SMS_SERVICE_NAME.equals(alertServiceName)) {
         return new MessageBirdAlertSendServiceImpl();
      }
      throw new IllegalStateException("Unknown alert-send-service '" + alertServiceName + "'");
   }
}
