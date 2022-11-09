package com.aquabasilea.alerting.send;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.api.factory.AlertSendServiceFactory;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.config.AlertSendConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * The {@link BasicAlertSender} sends an alert configured by a {@link AlertSendConfig} to one or more subscribers
 */
public class BasicAlertSender {

   private static final Logger LOG = LoggerFactory.getLogger(BasicAlertSender.class);
   protected final Function<AlertSendConfig, AlertSendService> alertServiceFunction;
   protected final AlertSendConfigProvider configProvider;

   /**
    * Constructor only for testing purpose
    */
   public BasicAlertSender(AlertSendConfigProvider configProvider, Function<AlertSendConfig, AlertSendService> alertServiceFunction) {
      this.configProvider = configProvider;
      this.alertServiceFunction = alertServiceFunction;
   }

   /**
    * Default constructor. Uses the ALERT_API_CONST_FILE and the {@link AlertSendServiceFactory} in order to create an {@link AlertSendService}
    */
   public BasicAlertSender(AlertSendConfigProvider configProvider) {
      this(configProvider, alertSendConfig -> AlertSendServiceFactory.getAlertSendService4Name(alertSendConfig.getAlertServiceName()));
   }

   /**
    * Sends the given text message to the configured receivers, defined in the {@link AlertSendConfig}
    *
    * @param alertSendInfos the {@link AlertSendInfos} with message to send and receivers
    */
   public void sendMessage(AlertSendInfos alertSendInfos) {
      AlertSendConfig alertSendConfig = configProvider.getAlertSendConfig();
      try {
         getAlertSendApi(alertSendConfig).sendAlert(alertSendConfig, alertSendInfos);
      } catch (AlertSendException e) {
         LOG.error(String.format("Sending of alert '%s' failed!", alertSendInfos.msg()), e);
      }
   }

   private AlertSendService getAlertSendApi(AlertSendConfig alertSendConfig) {
      return alertServiceFunction.apply(alertSendConfig);
   }
}
