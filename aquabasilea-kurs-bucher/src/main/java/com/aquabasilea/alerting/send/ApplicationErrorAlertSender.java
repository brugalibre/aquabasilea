package com.aquabasilea.alerting.send;

import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.config.AlertSendConfigProvider;
import com.aquabasilea.alerting.config.AlertSendConfigProviderImpl;

/**
 * The {@link ApplicationErrorAlertSender} sends an alert configured by a {@link AlertSendConfig} to one or more subscribers
 */
public class ApplicationErrorAlertSender extends BasicAlertSender {

   private ApplicationErrorAlertSender(AlertSendConfigProvider configProvider) {
      super(configProvider);
   }

   public static ApplicationErrorAlertSender of() {
      return new ApplicationErrorAlertSender(AlertSendConfigProviderImpl.of());
   }

   /**
    * Sends an error message regarding a failure in the application
    * This message is send to the administrator of the application
    *
    * @param errorMsg the error message
    */
   public void sendApplicationErrorMessage(String errorMsg) {
      AlertSendInfos alertSendInfos = new AlertSendInfos(errorMsg, configProvider.getAlertSendConfig().getOnApplicationErrorReceivers());
      super.sendMessage(alertSendInfos);
   }
}
