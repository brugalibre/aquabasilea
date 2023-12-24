package com.aquabasilea.migrosapi.service.util.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ListTypeReader {
   private ListTypeReader() {
   }

   /**
    * Reads a generic list from the given json-Body
    *
    * @param bodyAsString the json body which contains a generic, unnamed list of values
    * @param objectMapper the {@link ObjectMapper} which parses the json
    * @param type         the specific type from a single list value
    * @return a list of entries which are read from the json-body
    * @throws JsonProcessingException when the body could not be parsed
    */
   public static <T> List<T> readListValue(String bodyAsString, ObjectMapper objectMapper, Class<T> type) throws JsonProcessingException {
      if (bodyAsString.trim().isEmpty()) {
         return List.of();
      }
      return objectMapper.readValue(bodyAsString, objectMapper
              .getTypeFactory()
              .constructCollectionType(List.class, type));
   }
}
