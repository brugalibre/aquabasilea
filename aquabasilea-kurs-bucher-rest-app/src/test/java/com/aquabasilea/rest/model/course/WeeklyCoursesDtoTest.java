package com.aquabasilea.rest.model.course;

import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.Course.CourseBuilder;
import com.aquabasilea.course.user.WeeklyCourses;
import com.aquabasilea.rest.model.course.user.WeeklyCoursesDto;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WeeklyCoursesDtoTest {

   @Test
   void getWeeklyCoursesDto() {

      // Given
      String firstCourseId = "1";
      String secondCourseId = "2";
      String thirdCourseId = "3";
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course currentCourse = CourseBuilder.builder()
              .withTimeOfTheDay("10:15")
              .withDayOfWeek(DayOfWeek.SUNDAY)
              .withCourseName("Kurs-abc")
              .withId(thirdCourseId)
              .build();
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withTimeOfTheDay("15:15")
              .withDayOfWeek(DayOfWeek.WEDNESDAY)
              .withCourseName("Kurs-abc")
              .withId(secondCourseId)
              .build(), currentCourse, CourseBuilder.builder()
              .withTimeOfTheDay("10:15")
              .withDayOfWeek(DayOfWeek.WEDNESDAY)
              .withCourseName("Kurs-abc")
              .withId(firstCourseId)
              .build()));

      // When
      WeeklyCoursesDto weeklyCoursesDto = WeeklyCoursesDto.of(weeklyCourses, currentCourse, Locale.GERMAN);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).isCurrentCourse(), is(true));
   }
}