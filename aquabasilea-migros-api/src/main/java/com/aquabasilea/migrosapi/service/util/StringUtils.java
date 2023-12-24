package com.aquabasilea.migrosapi.service.util;

public final class StringUtils {
   private StringUtils() {
      // private
   }

   public static boolean isNotEmpty(String bearerToken) {
      return bearerToken != null && !bearerToken.isEmpty();
   }
}
