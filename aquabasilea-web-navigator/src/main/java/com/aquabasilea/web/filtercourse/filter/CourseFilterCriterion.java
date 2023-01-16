package com.aquabasilea.web.filtercourse.filter;

import java.util.List;

/**
 * A {@link CourseFilterCriterion} describes an element of a course which can be filtered
 * This includes the specific attribute of the course which has to be filtered, defined by a {@link FilterType}
 * as well as the value which is applied to the filter
 */
public record CourseFilterCriterion (FilterType filterType, List<String> filterValues){

   public static CourseFilterCriterion of(FilterType filterType, List<String> filterValues) {
      return new CourseFilterCriterion(filterType, filterValues);
   }

   public static CourseFilterCriterion of(FilterType filterType, String filterValue) {
      return new CourseFilterCriterion(filterType, List.of(filterValue));
   }
}
