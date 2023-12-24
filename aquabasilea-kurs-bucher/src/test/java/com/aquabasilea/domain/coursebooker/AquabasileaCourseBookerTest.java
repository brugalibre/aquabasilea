package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.application.security.model.UserContext;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.config.TestAquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.notification.alertsend.CourseBookingAlertSender;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.domain.coursebooker.model.state.CourseBookingState.*;
import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
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
   private static final String TEST_USER_ID2 = "321";
   private static final String PHONE_NR = "";
   private static final ConsumerUser CONSUMER_USER = new ConsumerUser(TEST_USER_ID, PHONE_NR);
   public static final String COURSE_INSTRUCTOR = "peter";
   public static final String TEST_COURSE = "TestCourse";
   public static final String TEST_COURSE_1 = "TestCourse1";
   public static final String TEST_COURSE_2 = "TestCourse2";

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @BeforeEach
   public void setUp() {
      cleanUp();
      courseLocationRepository.save(MIGROS_FITNESSCENTER_AQUABASILEA);
   }

   @AfterEach
   public void cleanUp() {
      weeklyCoursesRepository.deleteAll();
      courseLocationRepository.deleteAll();
   }

   @Test
   void resumePausedAppAndResumeAllPausedCourses_AllCoursesPaused() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID2)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE)
                      .withCourseDate(LocalDateTime.now().plusDays(2))
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .withIsPaused(true)
                      .build())
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE_2)
                      .withCourseDate(LocalDateTime.now().plusDays(2))
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .withIsPaused(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
              .build();
      tcb.aquabasileaCourseBooker.start();

      // When -> start booker, wait until it's paused (since there are no courses to wait for) and then resume
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> tcb.aquabasileaCourseBooker.isPaused());
      tcb.aquabasileaCourseBooker.pauseOrResume();// resume

      // Then
      List<Course> weeklyCourses = weeklyCoursesRepository.getByUserId(TEST_USER_ID2).getCourses();
      assertThat(weeklyCourses.get(0).getIsPaused(), is(false));
      assertThat(weeklyCourses.get(1).getIsPaused(), is(false));
      weeklyCoursesRepository.deleteAll();
   }

   @Test
   void resumePausedAppAndResumeAllPausedCourses_NotAllCoursesPaused() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID2)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE)
                      .withCourseDate(LocalDateTime.now().plusDays(2))
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .withIsPaused(true)
                      .build())
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE_2)
                      .withCourseDate(LocalDateTime.now().plusDays(2))
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .withIsPaused(false)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
              .build();
      tcb.aquabasileaCourseBooker.start();

      // When -> start booker, wait until it's idle, pause the course and then resume
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> tcb.aquabasileaCourseBooker.isIdle());
      tcb.aquabasileaCourseBooker.pauseOrResume();// pause
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> tcb.aquabasileaCourseBooker.isPaused());
      tcb.aquabasileaCourseBooker.pauseOrResume();// resume

      // Then
      List<Course> weeklyCourses = weeklyCoursesRepository.getByUserId(TEST_USER_ID2).getCourses();
      Course course1 = getCourseByName(weeklyCourses, TEST_COURSE);
      Course course2 = getCourseByName(weeklyCourses, TEST_COURSE_2);
      assertThat(course1.getIsPaused(), is(true));
      assertThat(course2.getIsPaused(), is(false));
      weeklyCoursesRepository.deleteAll();
   }

   @Test
   void testGoIdlePauseResumeAndStop() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      long courseDelayedInFuture = 5;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(2)
              .plusMinutes(courseDelayedInFuture);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE)
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
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
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      long courseDelayedInFutur = 5;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusMinutes(courseDelayedInFutur);
      // Minutes one because due to certain delay until we finally call the method the result is something
      // like 390s -> almost 5 Minutes. Duration.ofMinutes() results in 4 Minutes
      Long expectedMinutesLeft = courseDelayedInFutur - 1;
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE_1)
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .build())
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(TEST_COURSE)
                      .withCourseDate(courseDate)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
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
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(2);
      LocalDateTime expectedCurrentCourseDate = courseDate.plusDays(7);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
              .withCourseBookingStateChangedHandler(mock(CourseBookingStateChangedHandler.class))
              .addCourseDef(new CourseDef("id", TEST_USER_ID, courseDate, aquabasileaFitnessCenter, COURSE_NAME, COURSE_INSTRUCTOR))
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
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      String newCourseName = "NewCourse";
      LocalDateTime courseDate = LocalDateTime.now()
              .minusMinutes(20);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(COURSE_NAME)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .build())
              .withAquabasileaWebCourseBooker(mock(CourseBookerFacade.class))
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
      CourseBookerFacade courseBookerFacade = mock(CourseBookerFacade.class);
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .withAquabasileaWebCourseBooker(courseBookerFacade)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(aquabasileaCourseBooker::isPaused);

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, PAUSED)));
      assertThat(aquabasileaCourseBooker.getCurrentCourse(), is(nullValue()));
      verify(courseBookerFacade, never()).bookCourse(any());
   }

   @Test
   void testInitializeGoIdleDryRunAndBooking() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(5);//300s
      TestWebCourseBooker aquabasileaWebNavigator = spy(new TestWebCourseBooker(CourseBookResult.BOOKED));
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
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .build())
              .addCourseDef(new CourseDef("id1", TEST_USER_ID, courseDate, aquabasileaFitnessCenter, COURSE_NAME, COURSE_INSTRUCTOR))
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(duration2StartBookerEarlier)
              .withDuration2StartDryRunEarlier(duration2StartDryRunEarlier)
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.contains(STOP));

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_DRY_RUN, BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP)));
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_DRY_RUN, BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP)));
      assertThat(testCourseBookingStateChangedHandler.bookingStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - (duration2StartBookerEarlier.toMinutes() - (24 * 60)))));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt.getMinute(), is((int) (actualCourseDate.getMinute() - (duration2StartDryRunEarlier.toMinutes() - (24 * 60)))));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation());
      CourseBookContainer courseBookContainerDryRun = new CourseBookContainer(courseBookDetails, new BookingContext(true));
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator).bookCourse(eq(courseBookContainerDryRun));
      verify(aquabasileaWebNavigator).bookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKED, COURSE_NAME)), eq(BOOKING_DRY_RUN));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKED, COURSE_NAME)), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testInitializeGoIdleBookingAndBooking24HBeforeNoDryRun() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      TestWebCourseBooker aquabasileaWebNavigator = spy(new TestWebCourseBooker(CourseBookResult.BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      String zumba = "Zumba";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(zumba)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseDate(courseDate)
                      .withHasCourseDef(true)
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .build())
              .addCourseDef(new CourseDef("id2", TEST_USER_ID, courseDate, aquabasileaFitnessCenter, zumba, COURSE_INSTRUCTOR))
              .withAquabasileaWebCourseBooker(aquabasileaWebNavigator)
              .withCourseBookingStateChangedHandler(testCourseBookingStateChangedHandler)
              .withDuration2StartBookerEarlier(java.time.Duration.ofMinutes((24 * 60) + minutesOffset - 1))
              .withDuration2StartDryRunEarlier(java.time.Duration.ofMinutes((24 * 60) + AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes()))
              .build();
      AquabasileaCourseBooker aquabasileaCourseBooker = tcb.aquabasileaCourseBooker;
      testCourseBookingStateChangedHandler.setWhenBookingIsDoneRunnable(aquabasileaCourseBooker::stop);

      // When
      tcb.aquabasileaCourseBooker.start();
      await().atMost(new Duration(290, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.contains (STOP));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP)));
      assertThat(actualCourseDate, is(notNullValue()));
      assertThat(testCourseBookingStateChangedHandler.dryRunStartedAt, is(nullValue()));
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation());
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator).bookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKED, zumba)), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testInitializeGoIdleBooking_NoBookingBecauseNoCourseDef() {
      // Given
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      long minutesOffset = AquabasileaCourseBookerConfig.DURATION_TO_START_DRY_RUN_EARLIER.toMinutes() - 10;
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(minutesOffset);// No time left for a dry run, 24h before!
      TestWebCourseBooker aquabasileaWebNavigator = spy(new TestWebCourseBooker(CourseBookResult.BOOKED));
      TestCourseBookingStateChangedHandler testCourseBookingStateChangedHandler = new TestCourseBookingStateChangedHandler();
      String zumba = "Zumba";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withUserId(TEST_USER_ID)
              .addWeeklyCourse(CourseBuilder.builder()
                      .withCourseName(zumba)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(aquabasileaFitnessCenter)
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
      await().atMost(new Duration(290, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.contains(STOP));

      // Then
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      LocalDateTime actualCourseDate = currentCourse.getCourseDate();
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              actualCourseDate, currentCourse.getCourseLocation());
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(List.of(INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP)));
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(false));
      verify(aquabasileaWebNavigator, never()).bookCourse(eq(courseBookContainer));
      verify(tcb.courseBookingAlertSender).consumeResult(eq(CONSUMER_USER), eq(CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, zumba)), eq(CourseBookingState.BOOKING));
   }

   @Test
   void testGoingIdleWithCourse1RefreshCoursesAndBookNewCourse() {
      // Given
      // CourseDate 50' from now
      // This course can never be booked, since the test fails after 2' of waiting
      CourseLocation aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      LocalDateTime courseDate = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(50);
      String newCourseName = "neuerKurs";
      LocalDateTime newCourseDate = courseDate.minusMinutes(45);

      List<CourseBookingState> expectedStateHistory = new LinkedList<>(List.of(INIT, IDLE_BEFORE_DRY_RUN, REFRESH_COURSES, INIT, IDLE_BEFORE_DRY_RUN,
              BOOKING_DRY_RUN, INIT, IDLE_BEFORE_BOOKING, BOOKING, INIT, STOP));
      TestWebCourseBooker aquabasileaWebNavigator = spy(new TestWebCourseBooker(CourseBookResult.BOOKED));
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
                      .withCourseLocation(aquabasileaFitnessCenter)
                      .build())
              .addCourseDef(new CourseDef("id1", TEST_USER_ID, courseDate, aquabasileaFitnessCenter, COURSE_NAME, COURSE_INSTRUCTOR))
              .addCourseDef(new CourseDef("id2", TEST_USER_ID, newCourseDate, aquabasileaFitnessCenter, newCourseName, COURSE_INSTRUCTOR))
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

      // Pause the current course and add a new one with a CourseDate 5' from now. This results in a waiting from 1' min, since the booker starts a few mins earlier
      WeeklyCourses weeklyCourses = this.weeklyCoursesRepository.getByUserId(TEST_USER_ID);
      Course course = weeklyCourses.getCourses()
              .get(0);
      course.setIsPaused(true);
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName(newCourseName)
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseDate(newCourseDate)
              .withHasCourseDef(true)
              .withCourseLocation(aquabasileaFitnessCenter)
              .build());
      this.weeklyCoursesRepository.save(weeklyCourses);
      // and refresh the bookers courses
      aquabasileaCourseBooker.refreshCourses();
      await().atMost(new Duration(220, TimeUnit.SECONDS)).until(() -> testCourseBookingStateChangedHandler.stateHistory.contains(STOP));

      // Then
      assertThat(testCourseBookingStateChangedHandler.stateHistory, is(expectedStateHistory));
      assertThat(aquabasileaWebNavigator.effectivelyBookedCourse, is(newCourseName));
   }

   private static Course getCourseByName(List<Course> weeklyCourses, String courseName) {
      return weeklyCourses.stream()
              .filter(course -> course.getCourseName().equals(courseName))
              .findFirst()
              .orElseThrow(IllegalStateException::new);
   }

   private class TestCaseBuilder {
      private AquabasileaCourseBooker aquabasileaCourseBooker;

      private CourseBookingStateChangedHandler courseBookingStateChangedHandler;
      private final CourseDefRepository courseDefRepository;
      private final CourseBookingEndResultConsumer courseBookingEndResultConsumer;
      private final CourseBookingAlertSender courseBookingAlertSender;
      private CourseBookerFacade courseBookerFacade;
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

      private TestCaseBuilder withAquabasileaWebCourseBooker(CourseBookerFacade courseBookerFacade) {
         this.courseBookerFacade = courseBookerFacade;
         return this;
      }

      private TestCaseBuilder build() {
         requireNonNull(userId, "call 'withUserId first!");
         saveCourses(userId, courses);
         mockCourseDefs();

         AquabasileaCourseBookerConfig config = new TestAquabasileaCourseBookerConfig("config/test-aquabasilea-kurs-bucher-config.yml", duration2StartDryRunEarlier, duration2StartBookerEarlier);
         UserContext userContext = new UserContext(userId, PHONE_NR);
         this.aquabasileaCourseBooker = new AquabasileaCourseBooker(userContext, weeklyCoursesRepository, courseDefRepository, config,
                 getAquabasileaCourseBookerFacadeFactory());
         this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(courseBookingStateChangedHandler);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(courseBookingAlertSender);
         this.aquabasileaCourseBooker.addCourseBookingEndResultConsumer(courseBookingEndResultConsumer);
         new AquabasileaCourseBookerExecutor(aquabasileaCourseBooker, "test-user-id");
         return this;
      }

      private CourseBookerFacadeFactory getAquabasileaCourseBookerFacadeFactory() {
         CourseBookerFacadeFactory courseBookerFacadeFactory = mock(CourseBookerFacadeFactory.class);
         when(courseBookerFacadeFactory.createCourseBookerFacade(any(), any())).thenReturn(courseBookerFacade);
         return courseBookerFacadeFactory;
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

   private static class TestWebCourseBooker implements CourseBookerFacade {
      private final CourseBookResult courseBookResult;
      private String effectivelyBookedCourse;

      private TestWebCourseBooker(CourseBookResult courseBookResult) {
         this.courseBookResult = courseBookResult;
      }

      @Override
      public CourseBookingResultDetails bookCourse(CourseBookContainer courseBookContainer) {
         CourseBookDetails courseBookDetails = courseBookContainer.courseBookDetails();
         this.effectivelyBookedCourse = courseBookDetails.courseName();
         return CourseBookingResultDetailsImpl.of(courseBookResult, courseBookDetails.courseName(), null);
      }

      @Override
      public List<Course> getBookedCourses() {
         return List.of();
      }

      @Override
      public CourseCancelResult cancelCourses(String bookingId) {
         return CourseCancelResult.COURSE_CANCELED;
      }

      @Override
      public List<CourseDef> getCourseDefs(String userId, List<CourseLocation> courseLocations) {
         return List.of();
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