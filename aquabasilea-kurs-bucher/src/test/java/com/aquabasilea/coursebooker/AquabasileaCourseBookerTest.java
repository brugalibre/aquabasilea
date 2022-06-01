package com.aquabasilea.coursebooker;

import com.aquabasilea.alerting.consumer.impl.AlertSender;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.config.TestAquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.persistence.config.AquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AquabasileaCourseBookerPersistenceConfig.class)
class AquabasileaCourseBookerTest {
   private static final String COURSE_NAME = "Test";

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @BeforeEach
   public void cleanUp() {
      weeklyCoursesRepository.deleteAll();
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
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse1")
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(DateUtil.getTimeAsString(LocalDateTime.now()))
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .build())
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse")
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(timeOfTheDay)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaWebCourseBooker.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      long minutesBeforeCourseBecomesBookable = aquabasileaCourseBooker.getDurationLeftBeforeCourseBecomesBookableSupplier().toMinutes();

      // Then;
      assertThat(minutesBeforeCourseBecomesBookable, is(expectedMinutesLeft));
   }

   @Test
   void testInitializeAndGoIdleBeforeDryRun() {
      // Given
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(2);
      LocalDateTime expectedCurrentCourseDate = courseDate.plusDays(7);
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(timeOfTheDay)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaWebCourseBooker.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
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
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(timeOfTheDay)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaWebCourseBooker.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      // Save new Course
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseName(), is(COURSE_NAME));
      WeeklyCourses weeklyCourses = this.weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.getCourses()
              .get(0)
              .setCourseName(newCourseName);
      this.weeklyCoursesRepository.save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      // Then
      currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseName(), is(newCourseName));
   }

   @Test
   void testInitializeButNoCoursesFoundSoPause() {
      // Given
      AquabasileaWebCourseBooker aquabasileaWebNavigator = mock(AquabasileaWebCourseBooker.class);
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(aquabasileaCourseBooker::isPaused);

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, PAUSED)));
      assertThat(aquabasileaCourseBooker.getCurrentCourse(), is(nullValue()));
      verify(aquabasileaWebNavigator, never()).selectAndBookCourse(any());
   }

   @Test
   void testInitializeGoIdleDryRunAndBooking() {
      // Given
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(5);//300s
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      java.time.Duration duration2StartBookerEarlier = java.time.Duration.ofMinutes(2);
      java.time.Duration duration2StartDryRunEarlier = java.time.Duration.ofMinutes(3);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(timeOfTheDay)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(duration2StartBookerEarlier)
              .withDuration2StartDryRunEarlier(duration2StartDryRunEarlier)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() -> aquabasileaWebNavigator.isBookingDone);

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.bookingStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - duration2StartBookerEarlier.toMinutes())));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - duration2StartDryRunEarlier.toMinutes())));
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_DRY_RUN, BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP, INIT)));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), actualCourseDate.getDayOfWeek(), currentCourse.getCourseLocation().getWebCourseLocation());
      verify(aquabasileaWebNavigator, times(2)).selectAndBookCourse(eq(courseBookDetails));
      verify(tcb.alertSender).consumeResult(eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(COURSE_NAME)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(BOOKING_DRY_RUN));
      verify(tcb.alertSender).consumeResult(eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(COURSE_NAME)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testInitializeGoIdleBookingAndBooking24HBeforeNoDryRun() {
      // Given
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      String timeOfTheDay = DateUtil.getTimeAsString(courseDate);
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(TestAquabasileaWebCourseBooker.noDryRun());
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      String zumba = "Zumba";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(zumba)
                      .withDayOfWeek(courseDate.getDayOfWeek())
                      .withTimeOfTheDay(timeOfTheDay)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(java.time.Duration.ofMinutes(minutesOffset - 1))
              .withDuration2StartDryRunEarlier(AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(210, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.containsAll(
              List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP, INIT)));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt, is(nullValue()));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), actualCourseDate.getDayOfWeek(), currentCourse.getCourseLocation().getWebCourseLocation());
      verify(aquabasileaWebNavigator).selectAndBookCourse(eq(courseBookDetails));
      verify(tcb.alertSender).consumeResult(eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(zumba)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(CourseBookingState.BOOKING));
   }

   private class TestCaseBuilder {
      private AquabasileaCourseBooker aquabasileaCourseBooker;

      private CourseBookingStateChangedHandler courseBookingStateChangedHandler;
      private final CourseBookingEndResultConsumer courseBookingEndResultConsumer;
      private final AlertSender alertSender;
      private AquabasileaWebCourseBooker aquabasileaWebNavigator;
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
         this.alertSender = mock(AlertSender.class);
      }

      private TestCaseBuilder withAquabasileaWebCourseBooker(AquabasileaWebCourseBooker aquabasileaWebNavigator) {
         this.aquabasileaWebNavigator = aquabasileaWebNavigator;
         return this;
      }

      private TestCaseBuilder build() {
         saveCourses(courses);

         AquabasileaCourseBookerSupplier courseBookerSupplier = new AquabasileaCourseBookerSupplier();
         Runnable threadRunnable = () -> courseBookerSupplier.aquabasileaCourseBooker.run();
         Thread aquabasileaCourseBookerThread = new Thread(threadRunnable);
         AquabasileaCourseBookerConfig config = new TestAquabasileaCourseBookerConfig("TEST_WEEKLY_COURSES_YML", duration2StartDryRunEarlier, duration2StartBookerEarlier);
         this.aquabasileaCourseBooker = new AquabasileaCourseBooker(weeklyCoursesRepository, config, () -> aquabasileaWebNavigator, aquabasileaCourseBookerThread);
         this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(courseBookingStateChangedHandler);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(alertSender);
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

      private class AquabasileaCourseBookerSupplier {
         private AquabasileaCourseBooker aquabasileaCourseBooker;
      }
   }

   private void saveCourses(List<Course> courses) {
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      weeklyCourses.setCourses(courses);
      this.weeklyCoursesRepository.save(weeklyCourses);
   }

   private static class TestAquabasileaWebCourseBooker implements AquabasileaWebCourseBooker {
      private final CourseClickedResult courseClickedResult;
      private boolean isDryRunDone;
      private boolean isBookingDone;

      private TestAquabasileaWebCourseBooker(CourseClickedResult courseClickedResult) {
         this.courseClickedResult = courseClickedResult;
         this.isDryRunDone = false;
         this.isBookingDone = false;
      }

      private static TestAquabasileaWebCourseBooker noDryRun() {
         TestAquabasileaWebCourseBooker testAquabasileaWebCourseBooker = new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED);
         testAquabasileaWebCourseBooker.isDryRunDone = true;
         return testAquabasileaWebCourseBooker;
      }

      @Override
      public CourseBookingEndResult selectAndBookCourse(CourseBookDetails courseBookDetails) {
         if (!this.isDryRunDone) {
            this.isDryRunDone = true;
         } else {
            this.isBookingDone = true;
         }
         return CourseBookingEndResultBuilder.builder()
                 .withCourseName(courseBookDetails.courseName())
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