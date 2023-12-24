package com.aquabasilea.migrosapi.api.v1.model;

import java.util.Map;

public final class MigrosApiV1Const {
   private MigrosApiV1Const() {
      //private
   }

   /**
    * Static headers which must be present in any http-request with migros
    */
   public static final Map<String, String> HEADERS = Map.of(
           "Origin", "https://www.activfitness.ch",
           "Referer", "https://www.activfitness.ch");
}
