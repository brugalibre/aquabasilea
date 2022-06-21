package com.aquabasilea.rest.model.course;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WeeklyCoursesDtoTest {

   @Test
   void getWeeklyCoursesDto() {

      // Given
      String secondCourseId = "1";
      String thirdCourseId = "2";
      String fourthCourseId = "3";
      String firstCourseId = "4";
      LocalDateTime firstCourseDate = LocalDateTime.now()
              .plusDays(1);
      LocalDateTime secondCourseDate = firstCourseDate
              .plusMinutes(10);
      LocalDateTime thirdCourseDate = firstCourseDate
              .plusDays(1);
      LocalDateTime fourthCourseDate = firstCourseDate
              .plusDays(7);
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course currentCourse = CourseBuilder.builder()
              .withCourseDate(thirdCourseDate)
              .withCourseName("Kurs-abc1")
              .withId(fourthCourseId)
              .build();
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseDate(secondCourseDate)
              .withCourseName("Kurs-abc2")
              .withId(thirdCourseId)
              .build(), currentCourse, CourseBuilder.builder()
              .withCourseDate(firstCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(secondCourseId)
              .build(), CourseBuilder.builder()
              .withCourseDate(fourthCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(firstCourseId)
              .build()));

      // When
      WeeklyCoursesDto weeklyCoursesDto = WeeklyCoursesDto.of(weeklyCourses, currentCourse, Locale.GERMAN);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).id(), is(fourthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).isCurrentCourse(), is(true));
   }
}