package com.aquabasilea.coursebooker;

import com.aquabasilea.course.AquabasileaWeeklyCourseConst;
import com.aquabasilea.course.Course;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.coursebooker.states.init.InitializationResult;
import com.aquabasilea.util.DateUtil;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaWebNavigator;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaWebNavigatorImpl;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Supplier;

import static com.aquabasilea.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.isNull;

/**
 * The {@link AquabasileaCourseBooker} is the heart of the aquabasilea-course-booking application
 */
public class AquabasileaCourseBooker implements Runnable {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);
   private static final int STAY_IDLE_INTERVAL = 500;

   private boolean isRunning;
   private CourseBookingState state;
   private InitializationResult initializationResult;
   private InitStateHandler initStateHandler;
   private Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSupplier;

   /**
    * Constructor only for testing purpose!
    *
    * @param aquabasileaWebNavigatorSup the {@link Supplier} for a {@link AquabasileaWebNavigator}
    */
   AquabasileaCourseBooker(AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig, Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSup, String testYmlFile) {
      this.aquabasileaWebNavigatorSupplier = aquabasileaWebNavigatorSup;
      init(aquabasileaCourseBookerConfig, testYmlFile);
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker}
    *
    * @param username           the users username
    * @param userPwd            the users password
    */
   public AquabasileaCourseBooker(String username, String userPwd) {
      this.aquabasileaWebNavigatorSupplier = () -> AquabasileaWebNavigatorImpl.createAndInitAquabasileaWebNavigator(username, userPwd, state == BOOKING_DRY_RUN, this::getTimeLeftBeforeCourseBecomesBookableSupplier);
      init(new AquabasileaCourseBookerConfig(), AquabasileaWeeklyCourseConst.WEEKLY_COURSES_YML);
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, String weeklyCoursesYmlFile) {
      this.initStateHandler = new InitStateHandler(weeklyCoursesYmlFile, bookerConfig);
      this.isRunning = true;
      setState(INIT);
   }

   @Override
   public void run() {
      while (isRunning) {
         handleCurrentState();
      }
   }

   public void stop() {
      this.isRunning = false;
      setState(STOP);
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
            getNextState();
            break;
         case STOP:
            stop();
            break;
         default:
            throw new IllegalStateException("Unhandled state '" + this.state + "'");
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
         setState(INIT);
      }
   }

   private CourseBookingEndResult bookCourse() {
      LOG.info("About going to {} the course '{}' at {}", state == BOOKING ? "book" : "dry-run the booking", getCurrentCourse().getCourseName(), DateUtil.toStringWithSeconds(LocalDateTime.now(), Locale.GERMAN));
      DayOfWeek dayOfWeek = DateUtil.getDayOfWeekFromInput(getCurrentCourse().getDayOfWeek(), Locale.GERMAN);
      return aquabasileaWebNavigatorSupplier.get().selectAndBookCourse(getCurrentCourse().getCourseName(), dayOfWeek);
   }

   long getTimeLeftBeforeCourseBecomesBookableSupplier() {
      long timeLeft = DateUtil.calcTimeLeftBeforeDate(getCurrentCourse().getCourseDate());
      LOG.info("getTimeLeftBeforeCourseBecomesBookableSupplier: {}ms ({}s) left", timeLeft, (timeLeft / 1000L));
      return timeLeft;
   }

   private void getNextState() {
      setState(CourseBookingState.getNextState(this.state));
   }

   public Course getCurrentCourse() {
      return isNull(this.initializationResult) ? null : this.initializationResult.getCurrentCourse();
   }

   private void setState(CourseBookingState newtState) {
      if (newtState != this.state) {
         LOG.info("Switched from state {} to new state {}", this.state, newtState);
         this.state = newtState;
      }
   }
}
