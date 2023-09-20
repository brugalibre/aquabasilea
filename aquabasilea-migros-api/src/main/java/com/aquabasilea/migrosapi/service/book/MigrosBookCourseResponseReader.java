package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.getcourse.response.MigrosBookCourseResponse;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;

import static com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static java.util.Objects.requireNonNull;

public class MigrosBookCourseResponseReader extends AbstractHttpResponseReader<MigrosBookCourseResponse> {
   @Override
   protected Class<MigrosBookCourseResponse> getResponseResultClass() {
      return MigrosBookCourseResponse.class;
   }

   @Override
   public ResponseWrapper<MigrosBookCourseResponse> createErrorResponse(Exception e, String url) {
      requireNonNull(e, "Exception must not be null");
      MigrosBookCourseResponse migrosBookCourseResponse = new MigrosBookCourseResponse();
      migrosBookCourseResponse.setMessage(e.getMessage());
      migrosBookCourseResponse.setCode(COURSE_NOT_SELECTED_EXCEPTION_OCCURRED.getErrorCode());
      return new ResponseWrapper<>(migrosBookCourseResponse, 500, e, url);
   }
}
