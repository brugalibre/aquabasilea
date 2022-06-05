package com.aquabasilea.model.course;

import com.aquabasilea.model.course.exception.CourseAlreadyExistsException;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyCoursesTest {

   public static final String COURSE_1 = "course1";
   public static final String COURSE_2 = "course2";

   @Test
   void addCourse() {

      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withDayOfWeek(courseDate.getDayOfWeek())
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_2)
              .withDayOfWeek(courseDate.getDayOfWeek())
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build());

      // Then
      assertThat(weeklyCourses.getCourses().size(), is(2));
   }

   @Test
   void addSameCourseCourseTwice() {

      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withDayOfWeek(courseDate.getDayOfWeek())
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      Executable ex = () -> weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withDayOfWeek(courseDate.getDayOfWeek())
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build());

      // Then
      assertThrows(CourseAlreadyExistsException.class, ex);
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
              .withCourseName(COURSE_1)
              .withDayOfWeek(courseDate.getDayOfWeek())
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
              .withCourseName(COURSE_1)
              .withDayOfWeek(courseDate.getDayOfWeek())
              .withTimeOfTheDay(dayOfTheWeek)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.changeCourse(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withDayOfWeek(courseDate.getDayOfWeek())
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
              .withDayOfWeek(DayOfWeek.WEDNESDAY)
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