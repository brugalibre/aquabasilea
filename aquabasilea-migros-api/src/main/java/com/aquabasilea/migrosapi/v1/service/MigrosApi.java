package com.aquabasilea.migrosapi.v1.service;

import com.aquabasilea.migrosapi.v1.model.book.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;

/**
 * The {@link MigrosApi} is the one who handles api calls from and to the migros-api
 * <b>Note:</b> The {@link MigrosApi} is not multi-threading capable! Each request should happen in the same Thread
 */
public interface MigrosApi {

   /**
    * Books the course for the given {@link MigrosApiBookCourseRequest}
    *
    * @param authenticationContainer    the {@link AuthenticationContainer} which defines credentials for a given user
    * @param migrosApiBookCourseRequest the request which defines the course to book
    * @return a {@link MigrosApiBookCourseResponse} with the result of the booking request
    */
   MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer, MigrosApiBookCourseRequest migrosApiBookCourseRequest);

   /**
    * Searches all booked courses for the user which is authenticated by the given {@link AuthenticationContainer}
    *
    * @param authenticationContainer the {@link AuthenticationContainer} which defines credentials for a given user
    * @return a {@link MigrosApiGetBookedCoursesResponse} with the result of the found courses
    */
   MigrosApiGetBookedCoursesResponse getBookedCourses(AuthenticationContainer authenticationContainer);

   /**
    * Searches all courses which matches the criteria defined in the {@link MigrosApiGetCoursesRequest}
    *
    * @param migrosApiGetCoursesRequest the {@link MigrosApiGetCoursesRequest} which defines the search criteria for courses
    * @return a {@link MigrosApiGetCoursesResponse} with the result of the found courses
    */
   MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest);
}
