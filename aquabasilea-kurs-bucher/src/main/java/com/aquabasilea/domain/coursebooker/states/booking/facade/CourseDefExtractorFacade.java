
package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.domain.coursedef.update.CourseDefExtractionResult;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.coursedef.model.CourseDef;

import java.util.List;

/**
 * The {@link CourseDefExtractorFacade} is a facade hiding the implementation of the actual process of reading
 * the bookable {@link CourseDef}s from the specific api
 */
public interface CourseDefExtractorFacade {

   /**
    * Returns a CourseDefFetchResult which contains all bookable courses from the specific sports api
    *
    * @param userId          the id of the user which wants to fetch the courses
    * @param courseLocations the {@link CourseLocation}s which should be included in the search
    * @return a CourseDefFetchResult which contains the evaluated {@link CourseDef}s
    */
   CourseDefExtractionResult getCourseDefs(String userId, List<CourseLocation> courseLocations);
}
