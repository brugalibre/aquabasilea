package com.aquabasilea.domain.coursedef.update;

import com.aquabasilea.domain.course.model.LocalDateTimeBuilder;
import com.aquabasilea.domain.coursedef.model.CourseDef;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The {@link CourseDefUpdateDate} describes the day and time when the {@link CourseDefUpdaterScheduler} should schedule
 * and update the {@link CourseDef}s
 */
public class CourseDefUpdateDate {
   private final DayOfWeek dayOfWeek;
   private final String timeOfTheDay;

   /**
    * Default constructor, creates a {@link CourseDefUpdateDate} for today at 11pm
    */
   public CourseDefUpdateDate() {
      this(LocalDate.now().getDayOfWeek(), "23:00");
   }

   public CourseDefUpdateDate(DayOfWeek dayOfWeek, String timeOfTheDay) {
      this.dayOfWeek = dayOfWeek;
      this.timeOfTheDay = timeOfTheDay;
   }

   public LocalDateTime calculateCourseDefUpdateLocalDateTime() {
      return LocalDateTimeBuilder.createLocalDateTime(this.dayOfWeek, this.timeOfTheDay);
   }
}
