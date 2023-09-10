package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.book.response.MigrosCancelCourseResponse;
import com.brugalibre.common.http.service.response.AbstractHttpResponseReader;

public class MigrosCancelCourseResponseReader extends AbstractHttpResponseReader<MigrosCancelCourseResponse> {
   @Override
   protected Class<MigrosCancelCourseResponse> getResponseResultClass() {
      return MigrosCancelCourseResponse.class;
   }
}
