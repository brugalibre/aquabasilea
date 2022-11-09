package com.aquabasilea.model.course;

import com.aquabasilea.model.course.weeklycourses.exception.CourseAlreadyExistsException;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_2)
              .withCourseDate(courseDate)
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
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build()));

      // When
      Executable ex = () -> weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build());

      // Then
      assertThrows(CourseAlreadyExistsException.class, ex);
   }

   @Test
   void addCourseWithSameCourseDateButAWeakAheadAgain() {

      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      LocalDateTime courseDateAWeekAhead = LocalDateTime.now()
              .plusDays(7);
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build()));

      // When
      Executable ex = () -> weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDateAWeekAhead)
              .withId(UUID.randomUUID().toString())
              .build());

      // Then
      assertThrows(CourseAlreadyExistsException.class, ex);
   }

   @Test
   void removeCourseById() {
      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
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
      String courseId = "123";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build()));

      // When
      weeklyCourses.changeCourse(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withCourseDate(courseDate)
              .withId(courseId)
              .build());

      // Then
      assertThat(weeklyCourses.getCourses().get(0).getCourseName(), is(COURSE_1));// remains immutable
   }

   @Test
   void pauseResumeCourse() {

      // Given
      String courseId = "1";
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course course = CourseBuilder.builder()
              .withCourseDate(LocalDateTime.now())
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