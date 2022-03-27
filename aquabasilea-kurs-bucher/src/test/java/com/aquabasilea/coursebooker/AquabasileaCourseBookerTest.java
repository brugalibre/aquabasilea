package com.aquabasilea.coursebooker;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.Course.CourseBuilder;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.util.YamlUtil;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaWebNavigator;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class AquabasileaCourseBookerTest {
   private static final String TEST_WEEKLY_COURSES_YML = "courses/testWeeklyCourses.yml";

   @AfterEach
   public void cleanUp() {
      YamlUtil.save2File(new WeeklyCourses(), getPath2YmlFile());
   }

   @Test
   void testCalcTimeLeftCourseStarts5MinInFutur() {

      // Given
      long courseDelayedInFutur = 5;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(courseDelayedInFutur);
      // Minutes one because due to certain delay until we finally call the method the result is something
      // like 390s -> almost 5 Minutes. Duration.ofMinutes() results in 4 Minutes
      Long expectedMinutesLeft = courseDelayedInFutur - 1;
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse")
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(mock(AquabasileaWebNavigator.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBookerThread.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      long millisLeftBeforeCourseBecomesBookable = aquabasileaCourseBooker.getTimeLeftBeforeCourseBecomesBookableSupplier();
      long minutesBeforeCourseBecomesBookable = millisLeftBeforeCourseBecomesBookable / 1000 / 60;

      // Then;
      assertThat(minutesBeforeCourseBecomesBookable, is (expectedMinutesLeft));

   }
   @Test
   void testInitializeAndGoIdleBeforeDryRun() {
      // Given
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(2);
      LocalDateTime expectedCurrentCourseDate = courseDate.plusDays(7);
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("Test")
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(mock(AquabasileaWebNavigator.class))
              .withCourseBookingStateChangedHandler(spy(CourseBookingStateChangedHandler.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBookerThread.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseDate(), is(notNullValue()));
      assertThat(currentCourse.getCourseDate().getDayOfWeek(), is(expectedCurrentCourseDate.getDayOfWeek()));
      assertThat(currentCourse.getCourseDate().getHour(), is(expectedCurrentCourseDate.getHour()));
      assertThat(currentCourse.getCourseDate().getDayOfYear(), is(expectedCurrentCourseDate.getDayOfYear()));
      assertThat(currentCourse.getCourseDate().getMinute(), is(expectedCurrentCourseDate.getMinute()));
      assertThat(currentCourse.getCourseDate().getDayOfYear(), is(expectedCurrentCourseDate.getDayOfYear()));
   }

   private static class TestCaseBuilder {
      private AquabasileaCourseBooker aquabasileaCourseBooker;
      private Thread aquabasileaCourseBookerThread;

      private CourseBookingStateChangedHandler courseBookingStateChangedHandler;
      private AquabasileaWebNavigator aquabasileaWebNavigator;
      private List<Course> courses;
      private java.time.Duration duration2StartBookerEarlier;
      private java.time.Duration duration2StartDryRunEarlier;

      private TestCaseBuilder() {
         this.courses = new ArrayList<>();
         this.duration2StartBookerEarlier = java.time.Duration.ofSeconds(20);
         this.duration2StartDryRunEarlier = java.time.Duration.ofSeconds(20);
      }

      private TestCaseBuilder withAquabasileaWebNavigator(AquabasileaWebNavigator aquabasileaWebNavigator) {
         this.aquabasileaWebNavigator = aquabasileaWebNavigator;
         return this;
      }

      private TestCaseBuilder build() {
         if (!this.courses.isEmpty()) {
            writeWeeklyCourses2File();
         }
         AquabasileaCourseBookerConfig config = new AquabasileaCourseBookerConfig();
         this.aquabasileaCourseBooker = new AquabasileaCourseBooker(config, () -> aquabasileaWebNavigator, getPath2YmlFile(), Thread.currentThread());
         this.aquabasileaCourseBookerThread = new Thread(aquabasileaCourseBooker);
         return this;
      }

      private TestCaseBuilder withCourseBookingStateChangedHandler(CourseBookingStateChangedHandler courseBookingStateChangedHandler) {
         this.courseBookingStateChangedHandler = courseBookingStateChangedHandler;
         return this;
      }

      private void writeWeeklyCourses2File() {
         WeeklyCourses weeklyCourses = new WeeklyCourses();
         weeklyCourses.setCourses(courses);
         String absolutePath = getPath2YmlFile();
         YamlUtil.save2File(weeklyCourses, absolutePath);
      }

      public TestCaseBuilder addWeeklyCourse(Course course) {
         this.courses.add(course);
         return this;
      }
   }

   private static String getPath2YmlFile() {
      Path resourceDirectory = Paths.get("src", "test", "resources");
      return resourceDirectory.toFile().getAbsolutePath() + "/" + TEST_WEEKLY_COURSES_YML;
   }
}