package com.aquabasilea.web.filtercourse.filter;

import java.util.ArrayList;
import java.util.List;

public record CourseFilter(List<CourseFilterCriterion> courseFilterCriteria) {

   public List<CourseFilterCriterion> getCourseFilterCriteria() {
      return courseFilterCriteria;
   }

   public CourseFilter() {
      this(new ArrayList<>());
   }

   public static class CourseFilterBuilder {
      private final CourseFilter courseFilter;

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
