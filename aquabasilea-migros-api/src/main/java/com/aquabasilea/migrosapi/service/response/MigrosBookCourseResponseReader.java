package com.aquabasilea.migrosapi.service.response;

import com.aquabasilea.migrosapi.http.service.response.AbstractHttpResponseReader;
import com.aquabasilea.migrosapi.model.response.MigrosBookCourseResponse;

public class MigrosBookCourseResponseReader extends AbstractHttpResponseReader<MigrosBookCourseResponse> {
   @Override
   protected Class<MigrosBookCourseResponse> getResponseResultClass() {
      return MigrosBookCourseResponse.class;
   }

   @Override
   public MigrosBookCourseResponse createErrorResponse(Exception e, String url) {
      return null;
   }
}
