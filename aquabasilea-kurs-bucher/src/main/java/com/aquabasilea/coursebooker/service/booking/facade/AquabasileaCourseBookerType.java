package com.aquabasilea.coursebooker.service.booking.facade;

import com.aquabasilea.coursedef.model.CourseDef;

/**
 * The {@link AquabasileaCourseBookerType} defines different methods how {@link CourseDef}s can be extracted
 * Either via the aquabasilea-course-page or directly via the migros-api
 */
public enum AquabasileaCourseBookerType {
   /**
    * Uses the migros-rest-api to book a courses
    */
   MIGROS_API,
   /**
    * Uses the aquabasilea-course-page and a web-driver to book a courses
    */
   AQUABASILEA_WEB
}
