package com.aquabasilea.migrosapi.service.getcourse;

import com.aquabasilea.migrosapi.model.getcourse.response.MigrosGetCoursesResponse;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;

public class MigrosGetCoursesResponseReader extends AbstractHttpResponseReader<MigrosGetCoursesResponse> {
   @Override
   protected Class<MigrosGetCoursesResponse> getResponseResultClass() {
      return MigrosGetCoursesResponse.class;
   }

   @Override
   public ResponseWrapper createErrorResponse(Exception e, String url) {
      return ResponseWrapper.error(e, url);
   }
}
