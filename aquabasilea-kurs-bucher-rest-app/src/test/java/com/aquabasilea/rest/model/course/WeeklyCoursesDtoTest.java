package com.aquabasilea.rest.model.course;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
      LocalDateTime now = LocalDateTime.now();
      DayOfWeek dayOfWeek4CoursesWithSameDayOfWeek = now.getDayOfWeek();
      DayOfWeek otherDayOfWeek = now.plusDays(1).getDayOfWeek();
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      String todaysTimeOfTheDay = DateUtil.getTimeAsString(now.plusMinutes(10));
      Course currentCourse = CourseBuilder.builder()
              .withTimeOfTheDay(todaysTimeOfTheDay)
              .withDayOfWeek(otherDayOfWeek)
              .withCourseName("Kurs-abc")
              .withId(thirdCourseId)
              .build();
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withTimeOfTheDay("15:15")
              .withDayOfWeek(dayOfWeek4CoursesWithSameDayOfWeek)
              .withCourseName("Kurs-abc")
              .withId(secondCourseId)
              .build(), currentCourse, CourseBuilder.builder()
              .withTimeOfTheDay(todaysTimeOfTheDay)
              .withDayOfWeek(dayOfWeek4CoursesWithSameDayOfWeek)
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