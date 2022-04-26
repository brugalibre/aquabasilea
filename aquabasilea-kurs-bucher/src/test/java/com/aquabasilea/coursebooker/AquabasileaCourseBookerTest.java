package com.aquabasilea.coursebooker;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.Course.CourseBuilder;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.config.TestAquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.util.YamlUtil;
import com.aquabasilea.web.navigate.AquabasileaWebNavigator;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.selectcourse.result.CourseClickedResult;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

class AquabasileaCourseBookerTest {
   private static final String TEST_WEEKLY_COURSES_YML = "courses/testWeeklyCourses.yml";
   public static final String COURSE_NAME = "Test";

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
                      .withCourseName("TestCourse1")
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(DateUtil.getTimeAsString(LocalDateTime.now()))
                      .withIsPaused(true)
                      .build())
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

      long minutesBeforeCourseBecomesBookable = aquabasileaCourseBooker.getDurationLeftBeforeCourseBecomesBookableSupplier().toMinutes();

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
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(mock(AquabasileaWebNavigator.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
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

   @Test
   void testInitializeAndGoIdleRefreshCoursesAndGoInitAgain() {
      // Given
      String newCourseName = "NewCourse";
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(20);
      LocalDateTime expectedCurrentCourseDate = courseDate.plusDays(7);
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(mock(AquabasileaWebNavigator.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBookerThread.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      // Save new Course
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseName(), is(COURSE_NAME));
      writeWeeklyCourses2File(List.of(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withDayOfWeek(dayOfTheWeek)
              .withTimeOfTheDay(timeOfTheDay)
              .build()));

      aquabasileaCourseBooker.refreshCourses();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));


      // Then
      currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseName(), is(newCourseName));
   }

   @Test
   void testInitializeButNoCoursesFoundSoStop() {
      // Given
      AquabasileaWebNavigator aquabasileaWebNavigator = mock(AquabasileaWebNavigator.class);
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withAquabasileaWebNavigator(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.run();

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, STOP)));
      verify(aquabasileaWebNavigator, never()).selectAndBookCourse(any(), any());
   }

   @Test
   void testInitializeGoIdleDryRunAndBooking() {
      // Given
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(5);//300s
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      TestAquabasileaWebNavigator aquabasileaWebNavigator = spy(new TestAquabasileaWebNavigator(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      java.time.Duration duration2StartBookerEarlier = java.time.Duration.ofMinutes(2);
      java.time.Duration duration2StartDryRunEarlier = java.time.Duration.ofMinutes(3);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(duration2StartBookerEarlier)
              .withDuration2StartDryRunEarlier(duration2StartDryRunEarlier)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBookerThread.start();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() -> aquabasileaWebNavigator.isBookingDone);

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.bookingStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - duration2StartBookerEarlier.toMinutes())));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - duration2StartDryRunEarlier.toMinutes())));
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_DRY_RUN, BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP, INIT)));
      verify(aquabasileaWebNavigator, times(2)).selectAndBookCourse(eq(currentCourse.getCourseName()), eq(DateUtil.getDayOfWeekFromInput(currentCourse.getDayOfWeek(), Locale.GERMAN)));
   }

   @Test
   void testInitializeGoIdleBookingAndBooking24HBeforeNoDryRun() {
      // Given
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      String dayOfTheWeek = courseDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
      TestAquabasileaWebNavigator aquabasileaWebNavigator = spy(TestAquabasileaWebNavigator.noDryRun());
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      java.time.Duration duration2StartBookerEarlier = java.time.Duration.ofMinutes(2);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("Zumba")
                      .withDayOfWeek(dayOfTheWeek)
                      .withTimeOfTheDay(timeOfTheDay)
                      .build())
              .withAquabasileaWebNavigator(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(java.time.Duration.ofMinutes(minutesOffset - 1))
              .withDuration2StartDryRunEarlier(AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBookerThread.start();
      await().atMost(new Duration(210, TimeUnit.SECONDS)).until(() -> aquabasileaWebNavigator.isBookingDone);

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt, is(nullValue()));
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP, INIT)));
      verify(aquabasileaWebNavigator).selectAndBookCourse(eq(currentCourse.getCourseName()), eq(DateUtil.getDayOfWeekFromInput(currentCourse.getDayOfWeek(), Locale.GERMAN)));
   }

   private static class TestCaseBuilder {
      private AquabasileaCourseBooker aquabasileaCourseBooker;
      private Thread aquabasileaCourseBookerThread;

      private CourseBookingStateChangedHandler courseBookingStateChangedHandler;
      private final CourseBookingEndResultConsumer courseBookingEndResultConsumer;
      private AquabasileaWebNavigator aquabasileaWebNavigator;
      private final List<Course> courses;
      private java.time.Duration duration2StartBookerEarlier;
      private java.time.Duration duration2StartDryRunEarlier;

      private TestCaseBuilder() {
         this.courses = new ArrayList<>();
         this.courseBookingStateChangedHandler = courseBookingState -> {
         };
         this.courseBookingEndResultConsumer = (res, state) -> {
         };
         this.duration2StartBookerEarlier = java.time.Duration.ofSeconds(20);
         this.duration2StartDryRunEarlier = java.time.Duration.ofSeconds(20);
      }

      private TestCaseBuilder withAquabasileaWebNavigator(AquabasileaWebNavigator aquabasileaWebNavigator) {
         this.aquabasileaWebNavigator = aquabasileaWebNavigator;
         return this;
      }

      private TestCaseBuilder build() {
         writeWeeklyCourses2File(courses);

         AquabasileaCourseBookerSupplier courseBookerSupplier = new AquabasileaCourseBookerSupplier();
         Runnable threadRunnable = () -> courseBookerSupplier.aquabasileaCourseBooker.run();
         aquabasileaCourseBookerThread = new Thread(threadRunnable);
         AquabasileaCourseBookerConfig config = new TestAquabasileaCourseBookerConfig(TEST_WEEKLY_COURSES_YML, duration2StartDryRunEarlier, duration2StartBookerEarlier);
         this.aquabasileaCourseBooker = new AquabasileaCourseBooker(config, () -> aquabasileaWebNavigator, getPath2YmlFile(), aquabasileaCourseBookerThread);
         this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(courseBookingStateChangedHandler);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(courseBookingEndResultConsumer);
         courseBookerSupplier.aquabasileaCourseBooker = aquabasileaCourseBooker;
         return this;
      }

      private TestCaseBuilder withCourseBookingStateChangedHandler(CourseBookingStateChangedHandler courseBookingStateChangedHandler) {
         this.courseBookingStateChangedHandler = courseBookingStateChangedHandler;
         return this;
      }

      public TestCaseBuilder addWeeklyCourse(Course course) {
         this.courses.add(course);
         return this;
      }

      public TestCaseBuilder withDuration2StartBookerEarlier(java.time.Duration duration2StartBookerEarlier) {
         this.duration2StartBookerEarlier = duration2StartBookerEarlier;
         return this;
      }

      public TestCaseBuilder withDuration2StartDryRunEarlier(java.time.Duration duration2StartDryRunEarlier) {
         this.duration2StartDryRunEarlier = duration2StartDryRunEarlier;
         return this;
      }

      private static class AquabasileaCourseBookerSupplier {
         private AquabasileaCourseBooker aquabasileaCourseBooker;
      }
   }

   private static void writeWeeklyCourses2File(List<Course> courses) {
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      weeklyCourses.setCourses(courses);
      String absolutePath = getPath2YmlFile();
      YamlUtil.save2File(weeklyCourses, absolutePath);
   }

   private static String getPath2YmlFile() {
      Path resourceDirectory = Paths.get("src", "test", "resources");
      return resourceDirectory.toFile().getAbsolutePath() + "/" + TEST_WEEKLY_COURSES_YML;
   }

   private static class TestAquabasileaWebNavigator implements AquabasileaWebNavigator {
      private final CourseClickedResult courseClickedResult;
      private boolean isDryRunDone;
      private boolean isBookingDone;

      private TestAquabasileaWebNavigator(CourseClickedResult courseClickedResult) {
         this.courseClickedResult = courseClickedResult;
         this.isDryRunDone = false;
         this.isBookingDone = false;
      }

      private static TestAquabasileaWebNavigator noDryRun() {
         TestAquabasileaWebNavigator testAquabasileaWebNavigator = new TestAquabasileaWebNavigator(CourseClickedResult.COURSE_BOOKED);
         testAquabasileaWebNavigator.isDryRunDone = true;
         return testAquabasileaWebNavigator;
      }

      @Override
      public CourseBookingEndResult selectAndBookCourse(String courseName, DayOfWeek dayOfWeek) {
         if (!this.isDryRunDone) {
            this.isDryRunDone = true;
         } else {
            this.isBookingDone = true;
         }
         return CourseBookingEndResultBuilder.builder()
                 .withCourseName(courseName)
                 .withCourseClickedResult(courseClickedResult)
                 .build();
      }
   }

   private static class TestCourseBookingStateChangedHandler implements CourseBookingStateChangedHandler {

      private Runnable whenBookingIsDoneRunnable;
      private CourseBookingState prevBookingState;
      private final LinkedList<CourseBookingState> stateHistory;
      private LocalDateTime bookingStartedAt;
      private LocalDateTime dryRunStartedAt;

      private TestCourseBookingStateChangedHandler() {
         this.stateHistory = new LinkedList<>();
         this.whenBookingIsDoneRunnable = () -> {
         };
      }

      private void setWhenBookingIsDoneRunnable(Runnable whenBookingIsDoneRunnable) {
         this.whenBookingIsDoneRunnable = whenBookingIsDoneRunnable;
      }

      @Override
      public void onCourseBookingStateChanged(CourseBookingState courseBookingState) {
         if (courseBookingState == BOOKING) {
            this.bookingStartedAt = LocalDateTime.now();
         }
         if (courseBookingState == BOOKING_DRY_RUN) {
            this.dryRunStartedAt = LocalDateTime.now();
         }
         if (prevBookingState == BOOKING
                 && courseBookingState == INIT) {
            whenBookingIsDoneRunnable.run();
         }
         this.prevBookingState = courseBookingState;
         this.stateHistory.add(courseBookingState);
      }
   }
}