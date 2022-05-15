package com.aquabasilea.course.aquabasilea.update;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseDefUpdaterTest {

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Test
   void updateAquabasileaCourses() {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
      String timeOfTheDay = "10:15";
      String courseName = "Test";
      AquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, dayOfWeek, timeOfTheDay, courseName, 0);
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> aquabasileaCourseExtractor, courseDefRepository, new CoursesDefEntityMapperImpl());

      // When
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).dayOfWeek(), is(DayOfWeek.MONDAY));
      assertThat(allCourseDefs.get(0).timeOfTheDay(), is(timeOfTheDay));
   }

   @Test
   void updateAquabasileaCoursesUpdateAlreadyRunning() throws InterruptedException {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
      String timeOfTheDay = "10:15";
      String courseName = "Test";
      int timeout = 1000;
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, dayOfWeek, timeOfTheDay, courseName, timeout);
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> aquabasileaCourseExtractor, courseDefRepository, new CoursesDefEntityMapperImpl());

      // When
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));
      Thread.sleep(timeout / 2);
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));
      Thread.sleep(timeout );

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(aquabasileaCourseExtractor.amountOfInvocations, is(1));
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).dayOfWeek(), is(DayOfWeek.MONDAY));
      assertThat(allCourseDefs.get(0).timeOfTheDay(), is(timeOfTheDay));
   }

   @NotNull
   private TestAquabasileaCourseExtractor createNewTestAquabasileaCourseExtractor(CourseLocation courseLocation, DayOfWeek dayOfWeek, String timeOfTheDay, String courseName, long extractingDuration) {
      List<AquabasileaCourse> aquabasileaCourses = List.of(new AquabasileaCourse(dayOfWeek, timeOfTheDay, courseLocation.getWebCourseLocation(), courseName));
      return new TestAquabasileaCourseExtractor(aquabasileaCourses, extractingDuration);
   }

   private static class TestAquabasileaCourseExtractor implements AquabasileaCourseExtractor {

      private final long extractingDuration;
      private final List<AquabasileaCourse> extractedAquabasileaCourses;
      private int amountOfInvocations;

      public TestAquabasileaCourseExtractor(List<AquabasileaCourse> extractedAquabasileaCourses, long extractingDuration) {
         this.extractedAquabasileaCourses = extractedAquabasileaCourses;
         this.extractingDuration = extractingDuration;
         this.amountOfInvocations = 0;
      }

      @Override
      public ExtractedAquabasileaCourses extractAquabasileaCourses(List<com.aquabasilea.web.model.CourseLocation> list) {
         try {
            Thread.sleep(extractingDuration);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         this.amountOfInvocations++;
         return () -> extractedAquabasileaCourses;
      }
   }
}