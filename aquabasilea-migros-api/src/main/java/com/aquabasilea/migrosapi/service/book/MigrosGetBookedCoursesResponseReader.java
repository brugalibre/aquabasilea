package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.getcourse.response.MigrosResponseCourse;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.aquabasilea.migrosapi.service.util.read.ListTypeReader.readListValue;

public class MigrosGetBookedCoursesResponseReader extends AbstractHttpResponseReader<List<MigrosResponseCourse>> {

   @Override
   protected List<MigrosResponseCourse> readValue(String bodyAsString, ObjectMapper objectMapper) throws JsonProcessingException {
      return readListValue(bodyAsString, objectMapper, MigrosResponseCourse.class);
   }

   @Override
   protected Class<List<MigrosResponseCourse>> getResponseResultClass() {
      return null;// since we override the readValue-method, this method is not used anyway
   }
}
