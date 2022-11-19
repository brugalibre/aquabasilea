package com.aquabasilea.web.bookcourse.model;

import com.aquabasilea.web.model.CourseLocation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public record CourseBookDetails(String courseName, String courseInstructor, LocalDateTime courseDate, CourseLocation courseLocation) {

   public String getDayOfWeekName(Locale locale) {
      DayOfWeek dayOfWeek = courseDate.getDayOfWeek();
      return dayOfWeek.getDisplayName(TextStyle.FULL, locale);
   }

   public String courseLocationName() {
      return courseLocation.getCourseLocationName();
   }
}
