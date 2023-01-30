package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.model.request.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.model.response.api.MigrosApiBookCourseResponse;

/**
 * The {@link MigrosApi} is the one who handles api calls from and to the migros-api
 */
public interface MigrosApi {

   /**
    * Books the course for the given {@link MigrosApiBookCourseRequest}
    *
    * @param migrosApiBookCourseRequest the request which defines the course to book
    * @return a {@link MigrosApiBookCourseResponse} with the result of the booking request
    */
   MigrosApiBookCourseResponse bookCourse(MigrosApiBookCourseRequest migrosApiBookCourseRequest);

   /**
    * Searches all courses which matches the criteria defined in the {@link MigrosApiGetCoursesRequest}
    *
    * @param migrosApiGetCoursesRequest the {@link MigrosApiGetCoursesRequest} which defines the search criteria for courses
    * @return a {@link MigrosApiGetCoursesResponse} with the result of the found courses
    */
   MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest);
}
