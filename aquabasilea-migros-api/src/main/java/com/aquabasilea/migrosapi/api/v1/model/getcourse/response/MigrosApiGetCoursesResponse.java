package com.aquabasilea.migrosapi.api.v1.model.getcourse.response;

import com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiGetCoursesResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#getCourses(AuthenticationContainer, MigrosApiGetCoursesRequest)}
 *
 * @param courses    the found {@link MigrosCourse}
 * @param successful <code>true</code> if the request was successful and the given <code>course</code> attribute contains the actual courses or <code>false</code> if there was an exception on the api
 */
public record MigrosApiGetCoursesResponse(List<MigrosCourse> courses, boolean successful) {
   /**
    * Creates an empty {@link MigrosApiGetCoursesResponse}
    *
    * @return an empty {@link MigrosApiGetCoursesResponse}
    */
   public static MigrosApiGetCoursesResponse empty() {
      return new MigrosApiGetCoursesResponse(List.of(), false);
   }
}
