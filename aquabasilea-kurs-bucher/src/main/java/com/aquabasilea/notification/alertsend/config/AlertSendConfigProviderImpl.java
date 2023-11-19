package com.aquabasilea.notification.alertsend.config;

import com.aquabasilea.application.security.securestorage.SecretStorage;
import com.aquabasilea.application.security.securestorage.util.KeyUtils;
import com.brugalibre.notification.config.AlertSendConfig;
import com.brugalibre.notification.config.AlertSendConfigProvider;
import com.brugalibre.util.file.yml.YamlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class AlertSendConfigProviderImpl implements AlertSendConfigProvider {
   private static final Logger LOG = LoggerFactory.getLogger(AlertSendConfigProviderImpl.class);
   private final YamlService yamlService;
   private String alertConfigFile;
   private String pathToKeyStore;
   private String pathToRootKeyStore;

   @Autowired
   public AlertSendConfigProviderImpl(@Value("${application.configuration.alert-notification}") String alertConfigFile) {
      this(alertConfigFile, KeyUtils.AQUABASILEA_ALERT_KEYSTORE, KeyUtils.AQUABASILEA_KEYSTORE_STORAGE);
   }

   public AlertSendConfigProviderImpl(String alertConfigFile, String pathToKeyStore, String pathToRootKeyStore) {
      this.yamlService = new YamlService();
      this.pathToKeyStore = pathToKeyStore;
      this.pathToRootKeyStore = pathToRootKeyStore;
      this.alertConfigFile = alertConfigFile;
      LOG.info("Using value {} for 'alertConfigFile'", alertConfigFile);
   }

   @Override
   public AlertSendConfig getAlertSendConfig() {
      AlertSendConfig alertSendConfig = yamlService.readYaml(alertConfigFile, AlertSendConfig.class);
      Supplier<char[]> apiKeyProvider = new SecretStorage(pathToKeyStore, pathToRootKeyStore).getSecretSupplier4Alias(alertSendConfig.getAlertServiceName(), "".toCharArray());
      alertSendConfig.setApiKeyProvider(apiKeyProvider);
      return alertSendConfig;
   }
}
