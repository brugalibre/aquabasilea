package com.aquabasilea.coursebooker;

import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.callback.AuthenticationCallbackHandler;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursebooker.states.booking.BookingStateHandler;
import com.aquabasilea.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.coursebooker.states.init.InitializationResult;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.isNull;

/**
 * The {@link AquabasileaCourseBooker} is the heart of the aquabasilea-course-booking application
 */
public class AquabasileaCourseBooker implements Runnable, AuthenticationCallbackHandler {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);
   private static final int STAY_IDLE_INTERVAL = 500;

   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private boolean isRunning;
   private CourseBookingState state;
   private InitializationResult initializationResult;
   private InitStateHandler initStateHandler;
   private BookingStateHandler bookingStateHandler;

   private Thread courseBookerThread;
   private InfoString4StateEvaluator infoString4StateEvaluator;

   private List<CourseBookingStateChangedHandler> courseBookingStateChangedHandlers;
   private List<CourseBookingEndResultConsumer> courseBookingEndResultConsumers;

   /**
    * Constructor only for testing purpose!
    *
    * @param weeklyCoursesRepository the {@link WeeklyCoursesRepository}
    * @param courseDefRepository the {@link CourseDefRepository}
    * @param aquabasileaCourseBookerConfig the {@link AquabasileaCourseBookerConfig}
    * @param aquabasileaWebCourseBookerSupp the {@link Supplier} for a {@link AquabasileaWebCourseBooker}
    * @param courseBookerThread         the {@link Thread} which controls this {@link AquabasileaCourseBooker}
    */
   AquabasileaCourseBooker(WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository, AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig,
                           Supplier<AquabasileaWebCourseBooker> aquabasileaWebCourseBookerSupp, Thread courseBookerThread) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, aquabasileaWebCourseBookerSupp);
      init(aquabasileaCourseBookerConfig, courseDefRepository, courseBookerThread);
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker}
    *
    * @param weeklyCoursesRepository WeeklyCoursesRepository
    * @param courseDefRepository the {@link CourseDefRepository}
    * @param courseBookerThread      the {@link Thread} which controls this {@link AquabasileaCourseBooker}
    */
   public AquabasileaCourseBooker(WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                                  Thread courseBookerThread) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, getDummyAquabasileaWebNavigatorSupplier());
      init(new AquabasileaCourseBookerConfig(), courseDefRepository, courseBookerThread);
   }

   private void createNewAquabasileaWebNavigatorSupplier(String username, String userPwd) {
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, () -> AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, userPwd,
              state == BOOKING_DRY_RUN, this::getDurationLeftBeforeCourseBecomesBookableSupplier));
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, CourseDefRepository courseDefRepository, Thread courseBookerThread) {
      this.initStateHandler = new InitStateHandler(weeklyCoursesRepository, courseDefRepository, bookerConfig);
      this.isRunning = true;
      this.courseBookerThread = courseBookerThread;
      this.infoString4StateEvaluator = new InfoString4StateEvaluator(bookerConfig);
      this.courseBookingStateChangedHandlers = new ArrayList<>();
      this.courseBookingEndResultConsumers = new ArrayList<>();
      setState(PAUSED);
   }

   /**
    * Starts this {@link AquabasileaCourseBooker}
    */
   public void start() {
      this.courseBookerThread.start();
   }

   @Override
   public void run() {
      LOG.info("AquabasileaCourseBooker started");
      setState(INIT);
      while (isRunning) {
         handleCurrentState();
      }
      LOG.info("AquabasileaCourseBooker finished");
   }

   public void stop() {
      this.isRunning = false;
      setState(STOP);
      this.courseBookerThread.interrupt();
   }

   /**
    * Pauses or resumes this {@link AquabasileaCourseBooker}
    * <b>Note:</b> if it is resumed, the current state is set to IDLE_BEFORE_DRY_RUN
    * regardless if the state was IDLE_BEFORE_BOOKING in the first place
    */
   public void pauseOrResume() {
      if (this.isIdle() || this.isPaused()) {
         setState(this.isIdle() ? PAUSED : INIT);
         this.courseBookerThread.interrupt();
      }
   }

   public void refreshCourses() {
      if (this.isIdle()) {
         this.courseBookerThread.interrupt();
      }
   }

   /**
    * @return a String representing the current state of this {@link AquabasileaCourseBooker}
    */
   public String getInfoString4State() {
      return infoString4StateEvaluator.getInfoString4State(this.state, getCurrentCourse());
   }

   private void handleCurrentState() {
      switch (this.state) {
         case INIT:
            handleInitializeState();
            break;
         case IDLE_BEFORE_BOOKING:
         case IDLE_BEFORE_DRY_RUN:
            handleIdleState(this.initializationResult.getDurationUtilDryRunOrBookingBegin());
            break;
         case BOOKING: // fall through
         case BOOKING_DRY_RUN:
            CourseBookingEndResult courseBookingResult = bookingStateHandler.bookCourse(getCurrentCourse(), state);
            notifyResult2Consumers(courseBookingResult);
            getNextState();
            break;
         case STOP:
            stop();
            break;
         case PAUSED:
            pauseApp();
            break;
         default:
            throw new IllegalStateException("Unhandled state '" + this.state + "'");
      }
   }

   private void notifyResult2Consumers(CourseBookingEndResult courseBookingResult) {
      courseBookingEndResultConsumers.forEach(courseBookingEndResultConsumer -> courseBookingEndResultConsumer.consumeResult(courseBookingResult, this.state));
   }

   private void pauseApp() {
      while (isRunning) {
         try {
            Thread.sleep(STAY_IDLE_INTERVAL);
         } catch (InterruptedException e) {
            LOG.info("Interrupted during pausing!");
            break;
         }
      }
   }

   private void handleInitializeState() {
      LOG.info("Handling state {}", INIT);
      this.initializationResult = initStateHandler.evaluateNextCourseAndState();
      initStateHandler.saveUpdatedWeeklyCourses(initializationResult);
      setState(initializationResult.getNextCourseBookingState());
   }

   private void handleIdleState(Duration duration2StayIdle) {
      try {
         long timeStayIdle = duration2StayIdle.toMillis();
         LOG.info("Going idle for {}", duration2StayIdle);
         while (timeStayIdle > 0) {
            Thread.sleep(Math.min(timeStayIdle, STAY_IDLE_INTERVAL));
            timeStayIdle = timeStayIdle - STAY_IDLE_INTERVAL;
         }
         LOG.info("Done idle");
         getNextState();
      } catch (InterruptedException e) {
         LOG.error(this.getClass().getSimpleName() + " was interrupted!", e);
         // maybe we were paused externally -> don't overwrite that
         if (state != PAUSED) {
            setState(INIT);
         }
      }
   }

   Duration getDurationLeftBeforeCourseBecomesBookableSupplier() {
      long timeLeft = DateUtil.calcTimeLeftBeforeDate(getCurrentCourse().getCourseDate());
      LOG.info("getTimeLeftBeforeCourseBecomesBookableSupplier: {}ms ({}s) left", timeLeft, (timeLeft / 1000L));
      return Duration.ofMillis(timeLeft);
   }

   private void getNextState() {
      setState(CourseBookingState.getNextState(this.state));
   }

   public boolean isIdle() {
      return state == IDLE_BEFORE_DRY_RUN
              || state == IDLE_BEFORE_BOOKING;
   }

   public boolean isBookingCourse() {
      return state == BOOKING;
   }

   public boolean isBookingCourseDryRun() {
      return state == BOOKING_DRY_RUN;
   }

   public boolean isPaused() {
      return state == PAUSED;
   }

   @Override
   public void onUserAuthenticated(String username, Supplier<char[]> userPwdSupplier) {
      createNewAquabasileaWebNavigatorSupplier(username, String.valueOf(userPwdSupplier.get()));
   }

   public void addCourseBookingStateChangedHandler(CourseBookingStateChangedHandler courseBookingStateChangedHandler) {
      this.courseBookingStateChangedHandlers.add(courseBookingStateChangedHandler);
   }

   public void addCourseBookingEndResultConsumer(CourseBookingEndResultConsumer courseBookingEndResultConsumer) {
      this.courseBookingEndResultConsumers.add(courseBookingEndResultConsumer);
   }

   public Course getCurrentCourse() {
      return isNull(this.initializationResult) ? null : this.initializationResult.getCurrentCourse();
   }

   private void setState(CourseBookingState newtState) {
      if (newtState != this.state) {
         LOG.info("Switched from state {} to new state {}", this.state, newtState);
         this.state = newtState;
         this.courseBookingStateChangedHandlers
                 .forEach(courseBookingStateChangedHandler -> courseBookingStateChangedHandler.onCourseBookingStateChanged(this.state));
      }
   }

   private static Supplier<AquabasileaWebCourseBooker> getDummyAquabasileaWebNavigatorSupplier() {
      return () -> (courseBookDetails) -> CourseBookingEndResultBuilder.builder()
              .withCourseName(courseBookDetails.courseName())
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED)
              .withException(new AquabaslieaUserNotAuthenticatedException("No aquabasliea-user authentication was done!\nCall AquabasileaCourseBooker.onUserAuthenticated first!"))
              .build();
   }

   private static class AquabaslieaUserNotAuthenticatedException extends Exception {
      public AquabaslieaUserNotAuthenticatedException(String msg) {
         super(msg);
      }
   }
}
