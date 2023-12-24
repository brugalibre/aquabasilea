package com.aquabasilea.rest.model.course.coursedef;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.time.format.TextStyle;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDefDto(String courseName,
                           String courseInstructor,
                           String dayOfWeek,
                           String timeOfTheDay,
                           CourseLocationDto courseLocationDto,
                           LocalDateTime courseDefDate) {

   /**
    * Creates a new {@link CourseDefDto}
    *
    * @param courseDef for which a {@link CourseDefDto} is created
    * @return a new {@link CourseDefDto}
    */
   public static CourseDefDto of(CourseDef courseDef) {
      String dayOfWeekName = courseDef.courseDate()
              .getDayOfWeek()
              .getDisplayName(TextStyle.FULL, LocaleProvider.getDefaultLocale());
      return new CourseDefDto(courseDef.courseName(),
              courseDef.courseInstructor(),
              dayOfWeekName,
              DateUtil.getTimeAsString(courseDef.courseDate()),
              CourseLocationDto.of(courseDef.courseLocation()),
              courseDef.courseDate());
   }

   public String getCourseRepresentation() {
      String dateRep = DateUtil.toString(courseDefDate.toLocalDate(), LocaleProvider.getDefaultLocale());
      return String.format(TextResources.COURSE_REPRESENTATION, courseName, courseInstructor, dayOfWeek, dateRep,
              timeOfTheDay, courseLocationDto.name());
   }
}


