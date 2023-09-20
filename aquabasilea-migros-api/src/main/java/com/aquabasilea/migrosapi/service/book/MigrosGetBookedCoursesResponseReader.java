package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.getcourse.response.MigrosResponseCourse;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MigrosGetBookedCoursesResponseReader extends AbstractHttpResponseReader<List<MigrosResponseCourse>> {

   @Override
   protected List<MigrosResponseCourse> readValue(String bodyAsString, ObjectMapper objectMapper) throws JsonProcessingException {
      if (bodyAsString.trim().length() == 0) {
         return List.of();
      }
      return objectMapper.readValue(bodyAsString, objectMapper
              .getTypeFactory()
              .constructCollectionType(List.class, MigrosResponseCourse.class));
   }

   @Override
   protected Class<List<MigrosResponseCourse>> getResponseResultClass() {
      return null;// since we override the readValue-method, this method is not used anyway
   }
}
