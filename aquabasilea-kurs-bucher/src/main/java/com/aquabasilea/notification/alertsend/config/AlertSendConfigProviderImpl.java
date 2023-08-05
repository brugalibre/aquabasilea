package com.aquabasilea.notification.alertsend.config;

import com.aquabasilea.application.security.securestorage.SecretStorage;
import com.aquabasilea.application.security.securestorage.util.KeyUtils;
import com.brugalibre.notification.config.AlertSendConfig;
import com.brugalibre.notification.config.AlertSendConfigProvider;
import com.brugalibre.util.file.yml.YamlService;

import java.util.function.Supplier;

public record AlertSendConfigProviderImpl(String alertConfigFile,
                                          String pathToKeyStore) implements AlertSendConfigProvider {

   private static final YamlService YAML_SERVICE = new YamlService();
   private static final String ALERT_API_CONST_FILE = "config/alert/aquabasilea-alert-notification.yml";

   /**
    * Creates a default {@link AlertSendConfigProviderImpl} which uses the default {@link SecretStorage} location as well as
    * a config located at {@link AlertSendConfigProviderImpl#ALERT_API_CONST_FILE}
    *
    * @return a default {@link AlertSendConfigProviderImpl} which uses the default {@link SecretStorage} location as well as
    * * a config located at {@link AlertSendConfigProviderImpl#ALERT_API_CONST_FILE}
    */
   public static AlertSendConfigProvider of() {
      return new AlertSendConfigProviderImpl(ALERT_API_CONST_FILE, KeyUtils.AQUABASILEA_ALERT_KEYSTORE);
   }

   @Override
   public AlertSendConfig getAlertSendConfig() {
      AlertSendConfig alertSendConfig = YAML_SERVICE.readYaml(alertConfigFile, AlertSendConfig.class);
      Supplier<char[]> apiKeyProvider = new SecretStorage(pathToKeyStore).getSecretSupplier4Alias(alertSendConfig.getAlertServiceName(), "".toCharArray());
      alertSendConfig.setApiKeyProvider(apiKeyProvider);
      return alertSendConfig;
   }
}
