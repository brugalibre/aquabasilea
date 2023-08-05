package com.aquabasilea.domain.coursedef.update.facade;

import com.aquabasilea.domain.coursedef.model.CourseDef;

/**
 * The {@link CourseDefExtractorType} defines different methods how {@link CourseDef}s can be extracted
 * Either via the aquabasilea-course-page or directly via the migros-api
 */
public enum CourseDefExtractorType {
   /**
    * Uses the migros-rest-api to fetch all the courses
    */
   MIGROS_API,
   /**
    * Uses the aquabasilea-course-page and a web-driver to extract all courses
    */
   AQUABASILEA_WEB
}
