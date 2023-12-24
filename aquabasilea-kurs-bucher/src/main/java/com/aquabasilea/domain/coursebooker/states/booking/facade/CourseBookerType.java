package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.domain.coursedef.model.CourseDef;

/**
 * The {@link CourseBookerType} defines different methods how {@link CourseDef}s can be extracted
 * Either via the aquabasilea-course-page or directly via the migros-api
 */
public enum CourseBookerType {
   /**
    * Uses the migros-rest-api to book a courses
    */
   MIGROS_API,
}
