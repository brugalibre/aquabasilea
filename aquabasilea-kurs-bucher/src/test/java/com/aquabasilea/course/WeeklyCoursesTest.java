package com.aquabasilea.course;

import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.Course.CourseBuilder;
import com.aquabasilea.course.user.WeeklyCourses;
import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WeeklyCoursesTest {

   @Test
   void addCourse() {

      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName("course1")
              .withDayOfWeek(timeOfTheDay)
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName("course1")
              .withDayOfWeek(timeOfTheDay)
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build());

      // Then
      assertThat(weeklyCourses.getCourses().size(), is(2));
   }

   @Test
   void removeCourseById() {
      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName("course1")
              .withDayOfWeek(timeOfTheDay)
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.removeCourseById(courseId);

      // Then
      assertThat(weeklyCourses.getCourses().isEmpty(), is(true));
   }

   @Test
   void changeCourseById() {
      // Given
      String newCourseName = "newCourseName";
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName("course1")
              .withDayOfWeek(timeOfTheDay)
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.changeCourse(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withDayOfWeek(timeOfTheDay)
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build());

      // Then
      assertThat(weeklyCourses.getCourses().get(0).getCourseName(), is(newCourseName));
   }

   @Test
   void pauseResumeCourse() {

      // Given
      String courseId = "1";
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course course = CourseBuilder.builder()
              .withTimeOfTheDay("15:15")
              .withDayOfWeek("Mittwoch")
              .withCourseName("Kurs-abc")
              .withId(courseId)
              .build();
      weeklyCourses.setCourses(List.of(course));
      assertThat(course.getIsPaused(), is(false));

      // When
      weeklyCourses.pauseResumeCourse(courseId);

      // Then
      assertThat(course.getIsPaused(), is(true));
   }
}