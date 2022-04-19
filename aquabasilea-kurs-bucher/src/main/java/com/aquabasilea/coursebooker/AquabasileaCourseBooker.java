package com.aquabasilea.coursebooker;

import com.aquabasilea.course.AquabasileaWeeklyCourseConst;
import com.aquabasilea.course.Course;
import com.aquabasilea.coursebooker.callback.AuthenticationCallbackHandler;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.coursebooker.states.init.InitializationResult;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.navigate.AquabasileaWebNavigator;
import com.aquabasilea.web.navigate.AquabasileaWebNavigatorImpl;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import static com.aquabasilea.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.isNull;

/**
 * The {@link AquabasileaCourseBooker} is the heart of the aquabasilea-course-booking application
 */
public class AquabasileaCourseBooker implements Runnable, AuthenticationCallbackHandler {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);
   private static final int STAY_IDLE_INTERVAL = 500;

   private boolean isRunning;
   private CourseBookingState state;
   private InitializationResult initializationResult;
   private InitStateHandler initStateHandler;

   private Thread courseBookerThread;
   private InfoString4StateEvaluator infoString4StateEvaluator;

   private Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSupplier;
   private List<CourseBookingStateChangedHandler> courseBookingStateChangedHandlers;
   private List<CourseBookingEndResultConsumer> courseBookingEndResultConsumers;

   /**
    * Constructor only for testing purpose!
    *
    * @param aquabasileaWebNavigatorSup the {@link Supplier} for a {@link AquabasileaWebNavigator}
    * @param courseBookerThread         the {@link Thread} which controls this {@link AquabasileaCourseBooker}
    */
   AquabasileaCourseBooker(AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig, Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSup, String testYmlFile, Thread courseBookerThread) {
      this.aquabasileaWebNavigatorSupplier = aquabasileaWebNavigatorSup;
      init(aquabasileaCourseBookerConfig, testYmlFile, courseBookerThread);
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker}
    *
    * @param username           the users username
    * @param userPwd            the users password
    * @param courseBookerThread the {@link Thread} which controls this {@link AquabasileaCourseBooker}
    */
   public AquabasileaCourseBooker(String username, String userPwd, Thread courseBookerThread) {
      createNewAquabasileaWebNavigatorSupplier(username, userPwd);
      init(new AquabasileaCourseBookerConfig(), AquabasileaWeeklyCourseConst.WEEKLY_COURSES_YML, courseBookerThread);
   }

   private void createNewAquabasileaWebNavigatorSupplier(String username, String userPwd) {
      this.aquabasileaWebNavigatorSupplier = () -> AquabasileaWebNavigatorImpl.createAndInitAquabasileaWebNavigator(username, userPwd, state == BOOKING_DRY_RUN, this::getDurationLeftBeforeCourseBecomesBookableSupplier);
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, String weeklyCoursesYmlFile, Thread courseBookerThread) {
      this.initStateHandler = new InitStateHandler(weeklyCoursesYmlFile, bookerConfig);
      this.isRunning = true;
      this.courseBookerThread = courseBookerThread;
      this.infoString4StateEvaluator = new InfoString4StateEvaluator(bookerConfig);
      this.courseBookingStateChangedHandlers = new ArrayList<>();
      this.courseBookingEndResultConsumers = new ArrayList<>();
      setState(PAUSED);
   }

   @Override
   public void run() {
      setState(INIT);
      while (isRunning) {
         handleCurrentState();
      }
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
            handleIdleState(this.initializationResult.getTimeUtilDryRunOrBookingBegin());
            break;
         case BOOKING: // fall through
         case BOOKING_DRY_RUN:
            CourseBookingEndResult courseBookingResult = bookCourse();
            LOG.info("Course booking done. Result is {}", courseBookingResult);
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

   private void pauseApp(){
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
      setState(initializationResult.getNextCourseBookingState());
   }

   private void handleIdleState(long timeStayIdle) {
      try {
         LOG.info("Going idle for {}s", timeStayIdle);
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

   private CourseBookingEndResult bookCourse() {
      LOG.info("About going to {} the course '{}' at {}", state == BOOKING ? "book" : "dry-run the booking", getCurrentCourse().getCourseName(), DateUtil.toStringWithSeconds(LocalDateTime.now(), Locale.GERMAN));
      DayOfWeek dayOfWeek = DateUtil.getDayOfWeekFromInput(getCurrentCourse().getDayOfWeek(), Locale.GERMAN);
      return aquabasileaWebNavigatorSupplier.get().selectAndBookCourse(getCurrentCourse().getCourseName(), dayOfWeek);
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
}
