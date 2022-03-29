package com.aquabasilea.alerting.util;

import com.aquabasilea.alerting.config.AlertSendConfig;

import java.util.Base64;

public class AuthenticationUtil {
   private AuthenticationUtil() {
      // private
   }

   public static String getEncodedUsernameAndPassword(AlertSendConfig alertSendConfig) {
      String username = alertSendConfig.getUsername();
      String apiKey = alertSendConfig.getApiKey();
      return Base64.getEncoder().encodeToString((username + ":" + apiKey).getBytes());
   }
}
