package com.aquabasilea.migrosapi.service.getcenters;

import com.aquabasilea.migrosapi.model.getcenters.response.MigrosGetCentersResponse;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.aquabasilea.migrosapi.service.util.read.ListTypeReader.readListValue;

public class MigrosGetCentersResponseReader extends AbstractHttpResponseReader<List<MigrosGetCentersResponse>> {
   @Override
   protected Class<List<MigrosGetCentersResponse>> getResponseResultClass() {
      return null;
   }

   @Override
   protected List<MigrosGetCentersResponse> readValue(String bodyAsString, ObjectMapper objectMapper) throws JsonProcessingException {
      return readListValue(bodyAsString, objectMapper, MigrosGetCentersResponse.class);
   }
}
