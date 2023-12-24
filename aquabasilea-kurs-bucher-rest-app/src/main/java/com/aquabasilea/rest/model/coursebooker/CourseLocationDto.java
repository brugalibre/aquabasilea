package com.aquabasilea.rest.model.coursebooker;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseLocationDto(String name, String centerId, boolean isSelected) {
   public static CourseLocationDto of(CourseLocation courseLocation, boolean isSelected) {
      return new CourseLocationDto(courseLocation.name(), courseLocation.centerId(), isSelected);
   }

   public static CourseLocationDto of(CourseLocation courseLocation) {
      return new CourseLocationDto(courseLocation.name(), courseLocation.centerId(), false);
   }
}
