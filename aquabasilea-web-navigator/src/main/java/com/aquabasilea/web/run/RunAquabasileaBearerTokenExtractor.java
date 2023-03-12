package com.aquabasilea.web.run;

import com.aquabasilea.web.login.AquabasileaBearerTokenExtractor;
import com.aquabasilea.web.login.AquabasileaLogin;

public class RunAquabasileaBearerTokenExtractor {

   private static final String DEBUG_CONFIG_FILE = "config/debug-aquabasilea-kurs-bucher-config.yml";

   public static void main(String[] args) {
      try {
         run(args);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }


   private static void run(String[] args) {
      String username = args[0];
      char[] password = args[1].toCharArray();

      AquabasileaBearerTokenExtractor aquabasileaLogin = AquabasileaBearerTokenExtractor.createAquabasileaBearerTokenExtractor(username, password, DEBUG_CONFIG_FILE);
      String bearerToken = aquabasileaLogin.extractBearerToken();
      System.err.println(bearerToken);
   }
}
