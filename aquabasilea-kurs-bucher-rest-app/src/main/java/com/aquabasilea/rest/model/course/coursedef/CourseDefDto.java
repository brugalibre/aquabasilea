package com.aquabasilea.rest.model.course.coursedef;

import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.rest.i18n.LocalProvider;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.search.SearchableAttribute;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDefDto(@SearchableAttribute String courseName,
                           @SearchableAttribute String courseInstructor,
                           @SearchableAttribute String dayOfWeek,
                           @SearchableAttribute String timeOfTheDay,
                           @SearchableAttribute CourseLocationDto courseLocationDto,
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
              .getDisplayName(TextStyle.FULL, Locale.GERMAN);
      return new CourseDefDto(courseDef.courseName(),
              courseDef.courseInstructor(),
              dayOfWeekName,
              DateUtil.getTimeAsString(courseDef.courseDate()),
              CourseLocationDto.of(courseDef.courseLocation()),
              courseDef.courseDate());
   }

   public String getCourseRepresentation() {
      String dateRep = DateUtil.toString(courseDefDate.toLocalDate(), LocalProvider.getInstance().getCurrentLocale());
      return String.format(TextResources.COURSE_REPRESENTATION, courseName, courseInstructor, dayOfWeek, dateRep,
              timeOfTheDay, courseLocationDto.courseLocationName());
   }
}


