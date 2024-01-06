package com.aquabasilea.domain.course.weeklycourses;

import com.aquabasilea.domain.course.exception.CourseAlreadyExistsException;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.test.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyCoursesTest {

   public static final String COURSE_1 = "course1";
   public static final String COURSE_2 = "course2";
   public static final String COURSE_INSTRUCTOR = "courseInstructor";

   @Test
   void addCourse() {

      // Given
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      LocalDateTime courseDate = LocalDateTime.now();
      String courseId1 = "123";
      String courseId2 = "1234";
      weeklyCourses.setCourses(List.of(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId1)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
              .build()));

      // When
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_2)
              .withCourseDate(courseDate)
              .withId(courseId2)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
              .build());

      // Then
      assertThat(weeklyCourses.getCourses().size(), is(2));
      Course course2 = weeklyCourses.getCourseById(courseId2);
      assertThat(course2.getCourseName(), is(COURSE_2));
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
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
              .build()));

      // When
      Executable ex = () -> weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDate)
              .withId(courseId)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
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
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
              .withId(courseId)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .build()));

      // When
      Executable ex = () -> weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(COURSE_1)
              .withCourseDate(courseDateAWeekAhead)
              .withCourseLocation(TestConstants.FITNESSPARK_GLATTPARK)
              .withCourseInstructor(COURSE_INSTRUCTOR)
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
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
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
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
              .build()));

      // When
      weeklyCourses.changeCourse(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withCourseDate(courseDate)
              .withId(courseId)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
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
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
              .build();
      weeklyCourses.setCourses(List.of(course));
      assertThat(course.getIsPaused(), is(false));

      // When
      weeklyCourses.pauseResumeCourse(courseId);

      // Then
      assertThat(course.getIsPaused(), is(true));
   }
}