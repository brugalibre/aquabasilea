package com.aquabasilea.rest.model.course;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
      String lastCourseId = "4";
      LocalDateTime firstCourseDate = LocalDateTime.of(2022, 12, 12, 12, 15)
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
              .withId(lastCourseId)
              .build();
      List<Course> courses = new ArrayList<>(List.of(CourseBuilder.builder()
              .withCourseDate(secondCourseDate)
              .withCourseName("Kurs-abc2")
              .withId(secondCourseId)
              .build(), currentCourse, CourseBuilder.builder()
              .withCourseDate(firstCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(firstCourseId)
              .build(), CourseBuilder.builder()
              .withCourseDate(fourthCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(thirdCourseId)
              .build()));
      Collections.shuffle(courses);
      weeklyCourses.setCourses(courses);

      // When
      WeeklyCoursesDto weeklyCoursesDto = WeeklyCoursesDto.of(weeklyCourses, currentCourse, Locale.GERMAN);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).id(), is(lastCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).isCurrentCourse(), is(true));
   }

   @Test
   void getWeeklyCoursesDto2() {

      // Given
      String firstCourseId = "1";
      String secondCourseId = "2";
      String thirdCourseId = "3";
      String fourthCourseId = "4";
      String fifthCourseId = "5";
      String sixthCourseId = "6";
      LocalDateTime firstCourseDate = LocalDateTime.of(2022, 12, 12, 18, 15, 0);
      LocalDateTime secondCourseDate = firstCourseDate
              .plusMinutes(30);
      LocalDateTime thirdCourseDate = firstCourseDate
              .plusDays(2);
      LocalDateTime fourthCourseDate = thirdCourseDate
              .plusDays(1)
              .minusHours(8);
      LocalDateTime fifthCourseDate = thirdCourseDate
              .plusDays(1)
              .plusHours(1);
      LocalDateTime sixthCourseDate = thirdCourseDate
              .plusDays(2)
              .minusHours(6);
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course currentCourse = CourseBuilder.builder()
              .withCourseDate(thirdCourseDate)
              .withCourseName("Kurs-abc1")
              .withId(thirdCourseId)
              .build();
      List<Course> courses = new ArrayList<>(List.of(CourseBuilder.builder()
              .withCourseDate(secondCourseDate)
              .withCourseName("Kurs-abc2")
              .withId(secondCourseId)
              .build(), currentCourse, CourseBuilder.builder()
              .withCourseDate(firstCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(firstCourseId)
              .build(), CourseBuilder.builder()
              .withCourseDate(fourthCourseDate)
              .withCourseName("Kurs-abc4")
              .withId(fourthCourseId)
              .build(), CourseBuilder.builder()
              .withCourseDate(fifthCourseDate)
              .withCourseName("Kurs-5")
              .withId(fifthCourseId)
              .build(), CourseBuilder.builder()
              .withCourseDate(sixthCourseDate)
              .withCourseName("Kurs-6")
              .withId(sixthCourseId)
              .build()));
      Collections.shuffle(courses);
      weeklyCourses.setCourses(courses);

      // When
      WeeklyCoursesDto weeklyCoursesDto = WeeklyCoursesDto.of(weeklyCourses, currentCourse, Locale.GERMAN);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).id(), is(fourthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(4).id(), is(fifthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(5).id(), is(sixthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).isCurrentCourse(), is(true));
   }
}