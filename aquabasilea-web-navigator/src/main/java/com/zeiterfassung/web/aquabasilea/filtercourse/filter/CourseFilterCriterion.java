package com.zeiterfassung.web.aquabasilea.filtercourse.filter;

/**
 * A {@link CourseFilterCriterion} describes an element of a course which can be filtered
 * This includes the specific attribute of the course which has to be filtered, defined by a {@link FilterType}
 * as well as the value which is applied to the filter
 */
public class CourseFilterCriterion {
   private FilterType filterType;
   private String filterValue;

   private CourseFilterCriterion(FilterType filterType, String filterValue) {
      this.filterType = filterType;
      this.filterValue = filterValue;
   }

   public static CourseFilterCriterion of(FilterType filterType, String filterValue) {
      return new CourseFilterCriterion(filterType, filterValue);
   }

   public FilterType getFilterType() {
      return filterType;
   }

   public String getFilterValue() {
      return filterValue;
   }
}
