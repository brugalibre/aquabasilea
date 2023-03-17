package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.getcourse.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;

/**
 * The {@link MigrosApi} is the one who handles api calls from and to the migros-api
 * <b>Note:</b> The {@link MigrosApi} is not multi-threading capable! Each request should happen in the same Thread
 */
public interface MigrosApi {

   /**
    * Books the course for the given {@link MigrosApiBookCourseRequest}
    *
    * @param authenticationContainer the {@link AuthenticationContainer} which defines credentials for a given user
    * @param migrosApiBookCourseRequest the request which defines the course to book
    * @return a {@link MigrosApiBookCourseResponse} with the result of the booking request
    */
   MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer, MigrosApiBookCourseRequest migrosApiBookCourseRequest);

   /**
    * Searches all courses which matches the criteria defined in the {@link MigrosApiGetCoursesRequest}
    *
    * @param migrosApiGetCoursesRequest the {@link MigrosApiGetCoursesRequest} which defines the search criteria for courses
    * @return a {@link MigrosApiGetCoursesResponse} with the result of the found courses
    */
   MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest);
}
