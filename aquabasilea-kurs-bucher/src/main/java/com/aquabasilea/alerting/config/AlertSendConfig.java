package com.aquabasilea.alerting.config;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AlertSendConfig {
   private String apiKey;
   private String originatorName;
   private String alertServiceName;
   private String username;
   private String originator;
   private List<String> receivers;

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
      return apiKey;
   }

   public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
   }

   public String getOriginator() {
      return originator;
   }

   public void setOriginator(String originator) {
      this.originator = originator;
   }

   public List<String> getReceivers() {
      if (isNull(receivers)) {
         this.receivers = new ArrayList<>();
      }
      return receivers;
   }

   public void setReceivers(List<String> receivers) {
      this.receivers = receivers;
   }
}
