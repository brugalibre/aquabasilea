package com.aquabasilea.alerting.config;

import com.aquabasilea.security.securestorage.SecretStorage;
import com.aquabasilea.security.securestorage.util.KeyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

public class AlertSendConfig {
   private String originatorName;
   private String keyStoreLocation;
   private String alertServiceName;
   private String username;
   private String originator;
   private List<String> onApplicationErrorReceivers;

   public AlertSendConfig() {
      keyStoreLocation = KeyUtils.AQUABASILEA_ALERT_KEYSTORE;
   }

   public String getUsername() {
      return username;
   }

   public String getOriginatorName() {
      return originatorName;
   }

   public void setOriginatorName(String originatorName) {
      this.originatorName = originatorName;
   }

   public String getAlertServiceName() {
      return alertServiceName;
   }

   public void setAlertServiceName(String alertServiceName) {
      this.alertServiceName = alertServiceName;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getApiKey() {
      Supplier<char[]> apiKeyProvider = new SecretStorage(keyStoreLocation).getSecretSupplier4Alias(alertServiceName, "".toCharArray());
      return String.valueOf(apiKeyProvider.get());
   }

   public String getOriginator() {
      return originator;
   }

   public void setOriginator(String originator) {
      this.originator = originator;
   }

   public List<String> getOnApplicationErrorReceivers() {
      if (isNull(onApplicationErrorReceivers)) {
         this.onApplicationErrorReceivers = new ArrayList<>();
      }
      return onApplicationErrorReceivers;
   }

   public void setOnApplicationErrorReceivers(List<String> onApplicationErrorReceivers) {
      this.onApplicationErrorReceivers = onApplicationErrorReceivers;
   }

   public void setKeyStoreLocation(String keyStoreLocation) {
      this.keyStoreLocation = keyStoreLocation;
   }
}
