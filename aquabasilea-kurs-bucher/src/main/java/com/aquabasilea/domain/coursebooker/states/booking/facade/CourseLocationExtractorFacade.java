
package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.domain.courselocation.model.CourseLocation;

import java.util.List;

/**
 * The {@link CourseDefExtractorFacade} is a facade hiding the implementation of the actual process of reading
 * all {@link CourseLocation}s from the specific api
 */
public interface CourseLocationExtractorFacade {

   /**
    * Returns all course-locations from the concrete api
    *
    * @return the evaluated {@link CourseLocation}s
    */
   List<CourseLocation> getCourseLocations();
}
