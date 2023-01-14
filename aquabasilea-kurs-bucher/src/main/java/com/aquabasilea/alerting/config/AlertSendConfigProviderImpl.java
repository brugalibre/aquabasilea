package com.aquabasilea.alerting.config;

import com.aquabasilea.alerting.constants.AlertConstants;
import com.brugalibre.util.file.yml.YamlService;

public record AlertSendConfigProviderImpl(String alertConfigFile) implements AlertSendConfigProvider {

   private static final YamlService YAML_SERVICE = new YamlService();

   /**
    * Creates a default {@link AlertSendConfigProviderImpl} which uses a config while located at {@link AlertConstants#ALERT_API_CONST_FILE}
    *
    * @return a default {@link AlertSendConfigProviderImpl} which uses a config while located at {@link AlertConstants#ALERT_API_CONST_FILE}
    */
   public static AlertSendConfigProvider of() {
      return new AlertSendConfigProviderImpl(AlertConstants.ALERT_API_CONST_FILE);
   }

   @Override
   public AlertSendConfig getAlertSendConfig() {
      return YAML_SERVICE.readYaml(alertConfigFile, AlertSendConfig.class);
   }
}
