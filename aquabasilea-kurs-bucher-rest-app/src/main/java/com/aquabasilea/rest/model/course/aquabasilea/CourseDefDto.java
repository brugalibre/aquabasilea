package com.aquabasilea.rest.model.course.aquabasilea;

import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.search.SearchableAttribute;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.format.TextStyle;
import java.util.Locale;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDefDto(@SearchableAttribute String courseName, @SearchableAttribute String dayOfWeek,
                           @SearchableAttribute boolean gagg,
                           @SearchableAttribute String timeOfTheDay, @SearchableAttribute CourseLocationDto courseLocationDto) {

   /**
    * Creates a new {@link CourseDefDto}
    *
    * @param courseDef for which a {@link CourseDefDto} is created
    * @return a new {@link CourseDefDto}
    */
   public static CourseDefDto of(CourseDef courseDef) {
      return new CourseDefDto(courseDef.courseName(),
              courseDef.dayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), false,
              courseDef.timeOfTheDay(),
              CourseLocationDto.of(courseDef.courseLocation()));
   }

   public String getCourseRepresentation() {
      return String.format(TextResources.COURSE_REPRESENTATION, courseName, dayOfWeek,
              timeOfTheDay, courseLocationDto.courseLocationName());
   }
}


