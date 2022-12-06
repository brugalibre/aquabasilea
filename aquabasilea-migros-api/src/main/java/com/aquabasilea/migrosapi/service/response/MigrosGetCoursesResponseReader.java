package com.aquabasilea.migrosapi.service.response;

import com.aquabasilea.migrosapi.http.service.response.AbstractHttpResponseReader;
import com.aquabasilea.migrosapi.model.response.MigrosGetCoursesResponse;

public class MigrosGetCoursesResponseReader extends AbstractHttpResponseReader<MigrosGetCoursesResponse> {
   @Override
   protected Class<MigrosGetCoursesResponse> getResponseResultClass() {
      return MigrosGetCoursesResponse.class;
   }

   @Override
   public MigrosGetCoursesResponse createErrorResponse(Exception e, String url) {
      return new MigrosGetCoursesResponse(e, url);
   }
}
