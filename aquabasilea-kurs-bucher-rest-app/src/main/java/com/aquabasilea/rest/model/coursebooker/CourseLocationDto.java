package com.aquabasilea.rest.model.coursebooker;

import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.search.SearchableAttribute;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseLocationDto(@SearchableAttribute String courseLocationName, String courseLocationKey,
                                boolean isSelected) {
   public static CourseLocationDto of(CourseLocation courseLocation, boolean isSelected) {
      return new CourseLocationDto(courseLocation.getCourseLocationName(), courseLocation.name(), isSelected);
   }

   public static CourseLocationDto of(CourseLocation courseLocation) {
      return new CourseLocationDto(courseLocation.getCourseLocationName(), courseLocation.name(), false);
   }
}
