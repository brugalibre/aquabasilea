package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.config.TestAquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.BookingContext;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.notification.alertsend.CourseBookingAlertSender;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.domain.course.model.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class AquabasileaCourseBookerTest {
   private static final String COURSE_NAME = "Test";
   private static final String TEST_USER_ID = "123";
   private static final String PHONE_NR = "";
   private static final ConsumerUser CONSUMER_USER = new ConsumerUser(TEST_USER_ID, PHONE_NR);
   public static final String COURSE_INSTRUCTOR = "peter";

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @AfterEach
   public void cleanUp() {
      weeklyCoursesRepository.deleteAll();
   }

   @Test
   void testGoIdlePauseResumeAndStop() {
      // Given
      long courseDelayedInFuture = 5;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(2)
              .plusMinutes(courseDelayedInFuture);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
                      .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse")
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaCourseBookerFacade.class))
              .build();

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(5, TimeUnit.MINUTES)).until(() -> tcb.aquabasileaCourseBooker.isIdle());

      tcb.aquabasileaCourseBooker.pauseOrResume();
      await().atMost(new Duration(5, TimeUnit.MINUTES)).until(() -> tcb.aquabasileaCourseBooker.isPaused());
      tcb.aquabasileaCourseBooker.pauseOrResume();
      await().atMost(new Duration(5, TimeUnit.MINUTES)).until(() -> tcb.aquabasileaCourseBooker.isIdle());
      boolean wasPausedAfterResuming = tcb.aquabasileaCourseBooker.isPaused();
      tcb.aquabasileaCourseBooker.stop();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> !tcb.aquabasileaCourseBooker.isIdle());

      // Then;
      assertThat(wasPausedAfterResuming, is(false));
      assertThat(tcb.aquabasileaCourseBooker.getInfoString4State(), is(TextResources.INFO_TEXT_APP_STOPPED));
   }

   @Test
   void testCalcTimeLeftCourseStarts5MinInFuture() {

      // Given
      long courseDelayedInFutur = 5;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(courseDelayedInFutur);
      // Minutes one because due to certain delay until we finally call the method the result is something
      // like 390s -> almost 5 Minutes. Duration.ofMinutes() results in 4 Minutes
      Long expectedMinutesLeft = courseDelayedInFutur - 1;
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse1")
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .build())
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName("TestCourse")
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaCourseBookerFacade.class))
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
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaCourseBookerFacade.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .addCourseDef(new CourseDef("id", TEST_USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, COURSE_NAME, COURSE_INSTRUCTOR))
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
      assertThat(currentCourse.getCourseDate().getMinute(), is(expectedCurrentCourseDate.getMinute()));
      assertThat(currentCourse.getCourseDate().getDayOfYear(), is(expectedCurrentCourseDate.getDayOfYear()));
   }

   @Test
   void testInitializeAndGoIdleRefreshCoursesAndGoInitAgain() {
      // Given
      String newCourseName = "NewCourse";
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(20);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(AquabasileaCourseBookerFacade.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> nonNull(aquabasileaCourseBooker.getCurrentCourse()));

      // Save new Course
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      assertThat(currentCourse.getCourseName(), is(COURSE_NAME));
      WeeklyCourses weeklyCourses = this.weeklyCoursesRepository.getByUserId(TEST_USER_ID);
      weeklyCourses.getCourses()
              .get(0)
              .setCourseName(newCourseName);
      this.weeklyCoursesRepository.save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();

      // Then
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> currentCourseHasNewName(newCourseName, aquabasileaCourseBooker));
   }

   private boolean currentCourseHasNewName(String newCourseName, AquabasileaCourseBooker aquabasileaCourseBooker) {
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      return nonNull(currentCourse)
              && currentCourse.getCourseName().equals(newCourseName);
   }

   @Test
   void testInitializeButNoCoursesFoundSoPause() {
      // Given
      AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade = mock(AquabasileaCourseBookerFacade.class);
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .withAquabasileaWebCourseBooker(aquabasileaCourseBookerFacade)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(aquabasileaCourseBooker::isPaused);

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, PAUSED)));
      assertThat(aquabasileaCourseBooker.getCurrentCourse(), is(nullValue()));
      verify(aquabasileaCourseBookerFacade, never()).selectAndBookCourse(any());
   }

   @Test
   void testInitializeGoIdleDryRunAndBooking() {
      // Given
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(5);//300s
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      java.time.Duration duration2StartBookerEarlier = java.time.Duration.ofMinutes((24 * 60) + 3);
      java.time.Duration duration2StartDryRunEarlier = java.time.Duration.ofMinutes((24 * 60) + 4);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .addCourseDef(new CourseDef("id1", TEST_USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, COURSE_NAME, COURSE_INSTRUCTOR))
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(duration2StartBookerEarlier)
              .withDuration2StartDryRunEarlier(duration2StartDryRunEarlier)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() ->
              List.of(INIT, IDLE_BEFORE_DRY_RUN, BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP).equals(testCourseBookingStateChangedHandler.stateHistory));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.bookingStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - (duration2StartBookerEarlier.toMinutes() - (24 * 60)))));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - (duration2StartDryRunEarlier.toMinutes() - (24 * 60)))));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation().getCourseLocationName());
      CourseBookContainer courseBookContainerDryRun = new CourseBookContainer(courseBookDetails, new BookingContext(true));
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator).selectAndBookCourse(eq(courseBookContainerDryRun));
      verify(aquabasileaWebNavigator).selectAndBookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(COURSE_NAME)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(BOOKING_DRY_RUN));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(COURSE_NAME)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testInitializeGoIdleBookingAndBooking24HBeforeNoDryRun() {
      // Given
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      String zumba = "Zumba";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(zumba)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .addCourseDef(new CourseDef("id2", TEST_USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, zumba, COURSE_INSTRUCTOR))
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(java.time.Duration.ofMinutes((24 * 60) + minutesOffset - 1))
              .withDuration2StartDryRunEarlier(java.time.Duration.ofMinutes((24 * 60) + AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes()))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(290, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.containsAll(
              List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP, INIT)));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt, is(nullValue()));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation().getCourseLocationName());
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator).selectAndBookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(zumba)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build()), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testInitializeGoIdleBooking_NoBookingBecauseNoCourseDef() {
      // Given
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      String zumba = "Zumba";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(zumba)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(java.time.Duration.ofMinutes((24 * 60) + minutesOffset - 1))
              .withDuration2StartDryRunEarlier(java.time.Duration.ofMinutes((24 * 60) + AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes()))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(290, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.containsAll(
              List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, STOP)));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation().getCourseLocationName());
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator, never()).selectAndBookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingEndResultBuilder.builder()
              .withCourseName(zumba)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_SKIPPED)
              .build()), eq(CourseBookingState.BOOKING));
   }

   @Test
   void tesGoingIdleWithCourse1RefreshCoursesAndBookNewCourse() {
      // Given
      // CourseDate 50' from now
      // This course can never be booked, since the test fails after 2' of waiting
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(50);
      String newCourseName = "neuerKurs";
      LocalDateTime newCourseDate = courseDate.minusMinutes(45);

      List<CourseBookingState> expectedStateHistory = new LinkedList<>(List.of(INIT, IDLE_BEFORE_DRY_RUN, REFRESH_COURSES, INIT, IDLE_BEFORE_DRY_RUN,
              BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP));
      TestAquabasileaWebCourseBooker aquabasileaWebNavigator = spy(new TestAquabasileaWebCourseBooker(CourseClickedResult.COURSE_BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      java.time.Duration duration2StartBookerEarlier = java.time.Duration.ofMinutes((24 * 60) + 3);
      java.time.Duration duration2StartDryRunEarlier = java.time.Duration.ofMinutes((24 * 60) + 4);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .addCourseDef(new CourseDef("id1", TEST_USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, COURSE_NAME, COURSE_INSTRUCTOR))
              .addCourseDef(new CourseDef("id2", TEST_USER_ID, newCourseDate, MIGROS_FITNESSCENTER_AQUABASILEA, newCourseName, COURSE_INSTRUCTOR))
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(duration2StartBookerEarlier)
              .withDuration2StartDryRunEarlier(duration2StartDryRunEarlier)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      // start and wait until the booker is idle
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() ->
              nonNull(aquabasileaCourseBooker.getCurrentCourse())
                      && COURSE_NAME.equals(aquabasileaCourseBooker.getCurrentCourse().getCourseName())
                      && aquabasileaCourseBooker.isIdle());

      // Save new Course, CourseDate 5' from now. This results in a waiting from 1' min, since the booker starts a few mins earlier
      WeeklyCourses weeklyCourses = this.weeklyCoursesRepository.getByUserId(TEST_USER_ID);
      Course course = weeklyCourses.getCourses()
              .get(0);
      course.setCourseName(newCourseName);
      course.setCourseDate(newCourseDate);
      this.weeklyCoursesRepository.save(weeklyCourses);
      // and refresh the bookers courses
      aquabasileaCourseBooker.refreshCourses();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() -> expectedStateHistory.equals(testCourseBookingStateChangedHandler.stateHistory));

      // Then
      assertThat(aquabasileaWebNavigator.effectivelyBookedCourse, is(newCourseName));
   }

   private class TestCaseBuilder {
      private AquabasileaCourseBooker aquabasileaCourseBooker;

      private CourseBookingStateChangedHandler courseBookingStateChangedHandler;
      private final CourseDefRepository courseDefRepository;
      private final CourseBookingEndResultConsumer courseBookingEndResultConsumer;
      private final CourseBookingAlertSender courseBookingAlertSender;
      private AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade;
      private final List<Course> courses;
      private final List<CourseDef> courseDefs;

      private java.time.Duration duration2StartBookerEarlier;
      private java.time.Duration duration2StartDryRunEarlier;
      private String userId;

      private TestCaseBuilder() {
         this.courses = new ArrayList<>();
         this.courseDefs = new ArrayList<>();
         this.courseBookingStateChangedHandler = courseBookingState -> {
         };
         this.courseBookingEndResultConsumer = (consumerUser, res, state) -> {
         };
         this.duration2StartBookerEarlier = java.time.Duration.ofSeconds(20);
         this.duration2StartDryRunEarlier = java.time.Duration.ofSeconds(20);
         this.courseBookingAlertSender = mock(CourseBookingAlertSender.class);
         this.courseDefRepository = mock(CourseDefRepository.class);
      }

      private TestCaseBuilder withAquabasileaWebCourseBooker(AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade) {
         this.aquabasileaCourseBookerFacade = aquabasileaCourseBookerFacade;
         return this;
      }

      private TestCaseBuilder build() {
         requireNonNull(userId, "call 'withUserId first!");
         saveCourses(userId, courses);
         mockCourseDefs();

         AquabasileaCourseBookerConfig config = new TestAquabasileaCourseBookerConfig("config/test-aquabasilea-kurs-bucher-config.yml", duration2StartDryRunEarlier, duration2StartBookerEarlier);
         AquabasileaCourseBooker.UserContext userContext = new AquabasileaCourseBooker.UserContext(userId, "test", PHONE_NR, () -> new char[]{});
         this.aquabasileaCourseBooker = new AquabasileaCourseBooker(userContext, weeklyCoursesRepository, courseDefRepository, config, aquabasileaCourseBookerFacade);
         this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(courseBookingStateChangedHandler);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(courseBookingAlertSender);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(courseBookingEndResultConsumer);
         new AquabasileaCourseBookerExecutor(aquabasileaCourseBooker);
         return this;
      }

      private void mockCourseDefs() {
         when(courseDefRepository.getAllByUserId(eq(userId))).thenReturn(courseDefs);
      }

      private TestCaseBuilder withCourseBookingStateChangedHandler(CourseBookingStateChangedHandler courseBookingStateChangedHandler) {
         this.courseBookingStateChangedHandler = courseBookingStateChangedHandler;
         return this;
      }

      public TestCaseBuilder addWeeklyCourse(Course course) {
         this.courses.add(course);
         return this;
      }

      public TestCaseBuilder addCourseDef(CourseDef courseDef) {
         this.courseDefs.add(courseDef);
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

      public TestCaseBuilder withUserId(String userId) {
         this.userId = userId;
         return this;
      }
   }

   private void saveCourses(String userId, List<Course> courses) {
      WeeklyCourses weeklyCourses = new WeeklyCourses(userId);
      weeklyCourses.setCourses(courses);
      this.weeklyCoursesRepository.save(weeklyCourses);
   }

   private static class TestAquabasileaWebCourseBooker implements AquabasileaCourseBookerFacade {
      private final CourseClickedResult courseClickedResult;
      private String effectivelyBookedCourse;
      private List<String> canceledBookingIds;

      private TestAquabasileaWebCourseBooker(CourseClickedResult courseClickedResult) {
         this.courseClickedResult = courseClickedResult;
         this.canceledBookingIds = new ArrayList<>();
      }

      @Override
      public CourseBookingEndResult selectAndBookCourse(CourseBookContainer courseBookContainer) {
         CourseBookDetails courseBookDetails = courseBookContainer.courseBookDetails();
         this.effectivelyBookedCourse = courseBookDetails.courseName();
         return CourseBookingEndResultBuilder.builder()
                 .withCourseName(courseBookDetails.courseName())
                 .withCourseClickedResult(courseClickedResult)
                 .build();
      }

      @Override
      public List<Course> getBookedCourses() {
         return List.of();
      }

      @Override
      public void cancelCourses(String bookingId) {
         canceledBookingIds.add(bookingId);
      }
   }

   private static class TestCourseBookingStateChangedHandler implements CourseBookingStateChangedHandler {

      private Runnable whenBookingIsDoneRunnable;
      private CourseBookingState prevBookingState;
      private final List<CourseBookingState> stateHistory;
      private LocalDateTime bookingStartedAt;
      private LocalDateTime dryRunStartedAt;

      private TestCourseBookingStateChangedHandler() {
         this.stateHistory = new ArrayList<>();
         this.whenBookingIsDoneRunnable = () -> {
         };
      }

      private void setWhenBookingIsDoneRunnable(Runnable whenBookingIsDoneRunnable) {
         this.whenBookingIsDoneRunnable = whenBookingIsDoneRunnable;
      }

      @Override
      public void onCourseBookingStateChanged(CourseBookingState courseBookingState) {
         this.stateHistory.add(courseBookingState);
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
      }
   }
}