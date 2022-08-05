package com.aquabasilea.alerting.send;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.api.factory.AlertSendServiceFactory;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.aquabasilea.alerting.constants.AlertConstants.ALERT_API_CONST_FILE;

/**
 * The {@link BasicAlertSender} sends an alert configured by a {@link AlertSendConfig} to one or more subscribers
 */
public class BasicAlertSender {

   private static final Logger LOG = LoggerFactory.getLogger(BasicAlertSender.class);
   protected final String alertApiConstFile;
   protected final Function<AlertSendConfig, AlertSendService> alertServiceFunction;

   /**
    * Constructor only for testing purpose
    *
    * @param alertApiConstFile the file with the alert config
    */
   public BasicAlertSender(String alertApiConstFile, Function<AlertSendConfig, AlertSendService> alertServiceFunction) {
      this.alertApiConstFile = alertApiConstFile;
      this.alertServiceFunction = alertServiceFunction;
   }

   /**
    * Default constructor. Uses the ALERT_API_CONST_FILE and the {@link AlertSendServiceFactory} in order to create an {@link AlertSendService}
    */
   public BasicAlertSender() {
      this(ALERT_API_CONST_FILE, alertSendConfig -> AlertSendServiceFactory.getAlertSendService4Name(alertSendConfig.getAlertServiceName()));
   }

   /**
    * Sends the given text message to the received, configured by the {@link BasicAlertSender#alertApiConstFile}
    *
    * @param message the text message to send
    */
   public void sendMessage(String message) {
      AlertSendConfig alertSendConfig = YamlUtil.readYaml(alertApiConstFile, AlertSendConfig.class);
      try {
         getAlertSendApi(alertSendConfig).sendAlert(alertSendConfig, message);
      } catch (AlertSendException e) {
         LOG.error(String.format("Sending of alert '%s' failed!", message), e);
      }
   }

   private AlertSendService getAlertSendApi(AlertSendConfig alertSendConfig) {
      return alertServiceFunction.apply(alertSendConfig);
   }
}
