package com.aquabasilea.search;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ObjectTextSearchTest {

   @Test
   void testFilterCoursesWithSameCourseNameButDifferentLocation() {
      // Given
      LocalDate friday = LocalDate.of(2021, Month.NOVEMBER, 19);
      String filter = "functional";
      String filterWith2Words = "functional aqua";
      String functionalTraining = "Functional training";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourseDef(new CourseDef(friday, "13:15", CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining))
              .withCourseDef(new CourseDef(friday, "13:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, functionalTraining))
              .withCourseDef(new CourseDef(friday, "13:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "BeBo Fit (Beckenboden Fit)"))
              .withCourseDef(new CourseDef(friday.minusDays(4), "16:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant 2"))
              .withCourseDef(new CourseDef(friday.minusDays(1), "09:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant"))
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
      LocalDate friday = LocalDate.of(2021, Month.NOVEMBER, 19);
      String filter = "16:15 aqua functional";// 'aqua' should not mislead the best search result..
      String functionalTraining = "Functional training";
      String timeOfTheDayOfBestMatch = "16:15";
      String timeOfTheDayOfSecondBestMatch = "13:15";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourseDef(new CourseDef(friday, timeOfTheDayOfBestMatch, CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining))
              .withCourseDef(new CourseDef(friday, timeOfTheDayOfSecondBestMatch, CourseLocation.FITNESSPARK_HEUWAAGE, functionalTraining))
              .withCourseDef(new CourseDef(friday.minusDays(4), "10:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant 2"))
              .withCourseDef(new CourseDef(friday.minusDays(1), "09:15", CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, "Irrelevant"))
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

      private TestCaseBuilder() {
         this.courseDefDtos = new ArrayList<>();
         this.objectTextSearch = new ObjectTextSearch();
      }

      private TestCaseBuilder withCourseDef(CourseDef courseDef) {
         this.courseDefDtos.add(CourseDefDto.of(courseDef));
         return this;
      }

      private TestCaseBuilder build() {
         return this;
      }
   }
}