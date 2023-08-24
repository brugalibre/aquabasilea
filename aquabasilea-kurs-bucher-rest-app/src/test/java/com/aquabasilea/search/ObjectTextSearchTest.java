package com.aquabasilea.search;

import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.domain.course.LocalDateTimeBuilder;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ObjectTextSearchTest {

   private static final String USER_ID = "1234";
   public static final String COURSE_INSTRUCTOR = "Maja K.";

   @Test
   void testFilterCoursesWithSameCourseNameButDifferentLocation() {
      // Given
      LocalDateTime friday = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 19), LocalTime.of(13, 15));
      LocalDateTime monday = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 15), LocalTime.of(16, 15));
      LocalDateTime thursday = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 18), LocalTime.of(9, 15));
      String filter = "functional";
      String filterWith2Words = "functional aqua";
      String functionalTraining = "Functional training";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withMatchThreshold(0.677)
              .withCourseDef(new CourseDef("1", USER_ID, friday, CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining, COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("2", USER_ID, friday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, functionalTraining, COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("3", USER_ID, friday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "BeBo Fit (Beckenboden Fit)", COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("4", USER_ID, monday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant 2", COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("5", USER_ID, thursday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant", COURSE_INSTRUCTOR))
              .build();

      // When
      List<CourseDefDto> weightedCourseDefDtos4FilterWith2Words = tcb.objectTextSearch.getWeightedObjects4Filter(tcb.courseDefDtos, filterWith2Words);
      List<CourseDefDto> weightedCourseDefDtos4Filter = tcb.objectTextSearch.getWeightedObjects4Filter(tcb.courseDefDtos, filter);

      // Then
      assertThat(weightedCourseDefDtos4FilterWith2Words.get(0).courseLocationDto().courseLocationName(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA.getCourseLocationName()));
      assertThat(weightedCourseDefDtos4FilterWith2Words.get(1).courseLocationDto().courseLocationName(), is(CourseLocation.FITNESSPARK_HEUWAAGE.getCourseLocationName()));
      assertThat(weightedCourseDefDtos4Filter.get(0).courseLocationDto().courseLocationName(), is(CourseLocation.FITNESSPARK_HEUWAAGE.getCourseLocationName()));
      assertThat(weightedCourseDefDtos4Filter.get(1).courseLocationDto().courseLocationName(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA.getCourseLocationName()));
   }

   @Test
   void testFilterCoursesWithSameCourseNameLocationButDifferentTimes() {
      // Given
      String timeOfTheDayOfBestMatch = "16:15";
      String timeOfTheDayOfSecondBestMatch = "13:15";
      LocalDateTime friday_1615 = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 19), LocalDateTimeBuilder.createLocalTime(timeOfTheDayOfBestMatch));
      LocalDateTime friday_1315 = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 19), LocalDateTimeBuilder.createLocalTime(timeOfTheDayOfSecondBestMatch));
      LocalDateTime monday = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 15), LocalTime.of(10, 15));
      LocalDateTime thursday = LocalDateTime.of(LocalDate.of(2021, Month.NOVEMBER, 18), LocalTime.of(9, 15));
      String filter = "16:15 aqua functional";// 'aqua' should not mislead the best search result..
      String functionalTraining = "Functional training";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withMatchThreshold(0.7)
              .withCourseDef(new CourseDef("1", USER_ID, friday_1615, CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining, COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("2", USER_ID, friday_1315, CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining, COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("3", USER_ID, monday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant 2", COURSE_INSTRUCTOR))
              .withCourseDef(new CourseDef("4", USER_ID, thursday, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant", COURSE_INSTRUCTOR))
              .build();

      // When
      List<CourseDefDto> weightedCourseDefDtos4Filter = tcb.objectTextSearch.getWeightedObjects4Filter(tcb.courseDefDtos, filter);

      // Then
      assertThat(weightedCourseDefDtos4Filter.get(0).timeOfTheDay(), is(timeOfTheDayOfBestMatch));
      assertThat(weightedCourseDefDtos4Filter.get(1).timeOfTheDay(), is(timeOfTheDayOfSecondBestMatch));
   }

   private static final class TestCaseBuilder {
      private final ObjectTextSearch objectTextSearch;
      private final List<CourseDefDto> courseDefDtos;
      private double matchThreshold = 0.677;

      private TestCaseBuilder() {
         this.courseDefDtos = new ArrayList<>();
         this.objectTextSearch = new ObjectTextSearch(matchThreshold);
      }

      private TestCaseBuilder withCourseDef(CourseDef courseDef) {
         this.courseDefDtos.add(CourseDefDto.of(courseDef));
         return this;
      }

      private TestCaseBuilder withMatchThreshold(double matchThreshold) {
         this.matchThreshold = matchThreshold;
         return this;
      }

      private TestCaseBuilder build() {
         return this;
      }
   }
}