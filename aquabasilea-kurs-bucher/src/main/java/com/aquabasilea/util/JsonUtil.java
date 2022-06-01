package com.aquabasilea.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
   private JsonUtil() {
      // private
   }

   public static String createJsonFromObject(Object object) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
      objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
      try {
         return objectMapper.writeValueAsString(object);
      } catch (JsonProcessingException e) {
         throw new IllegalStateException(e);
      }
   }
}
