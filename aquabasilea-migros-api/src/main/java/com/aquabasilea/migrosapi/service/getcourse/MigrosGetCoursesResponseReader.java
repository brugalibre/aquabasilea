package com.aquabasilea.migrosapi.service.getcourse;

import com.aquabasilea.migrosapi.model.getcourse.response.MigrosGetCoursesResponse;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;

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
