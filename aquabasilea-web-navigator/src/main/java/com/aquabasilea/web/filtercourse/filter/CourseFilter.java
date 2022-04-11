package com.aquabasilea.web.filtercourse.filter;

import java.util.ArrayList;
import java.util.List;

public class CourseFilter {

   private final List<CourseFilterCriterion> courseFilterCriteria;

   private CourseFilter() {
      this.courseFilterCriteria = new ArrayList<>();
   }

   public List<CourseFilterCriterion> getCourseFilterCriteria() {
      return courseFilterCriteria;
   }

   public static class CourseFilterBuilder {
      private CourseFilter courseFilter;

      private CourseFilterBuilder() {
         this.courseFilter = new CourseFilter();
      }

      public CourseFilterBuilder addCourseFilterCriterion(CourseFilterCriterion courseFilterCriterion) {
         this.courseFilter.getCourseFilterCriteria().add(courseFilterCriterion);
         return this;
      }

      public CourseFilter build() {
         return courseFilter;
      }

      public static CourseFilterBuilder builder() {
         return new CourseFilterBuilder();
      }

   }

}
