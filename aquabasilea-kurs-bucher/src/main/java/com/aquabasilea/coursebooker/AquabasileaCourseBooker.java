package com.aquabasilea.coursebooker;

import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.service.booking.facade.AquabasileaCourseBookerFacadeFactory;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursebooker.states.booking.BookingStateHandler;
import com.aquabasilea.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.coursebooker.states.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.coursebooker.states.init.InitializationResult;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.brugalibre.domain.user.model.User;
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
public class AquabasileaCourseBooker implements Runnable {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);
   private static final int STAY_IDLE_INTERVAL = 500;

   private final BookingStateHandler bookingStateHandler;
   private final UserContext userContext;
   private boolean isRunning;
   private CourseBookingState state;
   private InitializationResult initializationResult;
   private InitStateHandler initStateHandler;

   private Thread courseBookerThread;
   private InfoString4StateEvaluator infoString4StateEvaluator;

   private List<CourseBookingStateChangedHandler> courseBookingStateChangedHandlers;
   private List<CourseBookingEndResultConsumer> courseBookingEndResultConsumers;

   /**
    * Constructor only for testing purpose!
    *
    * @param userContext                   the {@link UserContext}
    * @param weeklyCoursesRepository       the {@link WeeklyCoursesRepository}
    * @param courseDefRepository           the {@link CourseDefRepository}
    * @param aquabasileaCourseBookerConfig the {@link AquabasileaCourseBookerConfig}
    * @param aquabasileaCourseBookerFacade the {@link AquabasileaCourseBookerFacade} which implements the actual booking
    * @param courseBookerThread            the {@link Thread} which controls this {@link AquabasileaCourseBooker}
    */
   AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                           AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig,
                           AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade, Thread courseBookerThread) {
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, aquabasileaCourseBookerFacade);
      this.userContext = userContext;
      init(aquabasileaCourseBookerConfig, weeklyCoursesRepository, courseDefRepository, courseBookerThread);
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker}
    *
    * @param userContext                          the {@link UserContext} for the specific user
    * @param weeklyCoursesRepository              the {@link WeeklyCoursesRepository} to get and store a {@link WeeklyCourses}
    * @param courseDefRepository                  the {@link CourseDefRepository} for get and store the {@link CourseDef}s
    * @param aquabasileaCourseBookerFacadeFactory the {@link AquabasileaCourseBookerFacadeFactory} in order to create an {@link AquabasileaCourseBookerFacade}
    * @param courseBookerThread                   the Thread which controls this {@link AquabasileaCourseBooker}
    */
   public AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                                  AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory, Thread courseBookerThread) {
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, getAquabasileaCourseBookerFacade(aquabasileaCourseBookerFacadeFactory, userContext));
      this.userContext = userContext;
      init(new AquabasileaCourseBookerConfig(), weeklyCoursesRepository, courseDefRepository, courseBookerThread);
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, WeeklyCoursesRepository weeklyCoursesRepository,
                     CourseDefRepository courseDefRepository, Thread courseBookerThread) {
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
      LOG.info("AquabasileaCourseBooker started for user [{}]", this.userContext);
      setState(INIT);
      while (isRunning) {
         handleCurrentState();
      }
      LOG.info("AquabasileaCourseBooker finished for user [{}]", this.userContext);
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

   /**
    * Does the actual booking or dry-run of the current Course but only, if there is a {@link CourseDef}
    * for the given course. If the value of <code>withNotification</code> is <code>true</code> then all consumers listening
    * for the course-booking result are notified
    *
    * @param courseId           the id of the {@link Course} to book
    * @param courseBookingState the current {@link CourseBookingState}
    * @param withNotification   if <code>true</code> then the consumes are notified about the result of the booking.
    * @return a {@link CourseBookingEndResult} with details about the booking
    */
   public CourseBookingEndResult bookCourse(CourseBookingState courseBookingState, String courseId, boolean withNotification) {
      CourseBookingEndResult courseBookingEndResult = bookingStateHandler.bookCourse(userContext.id, courseId, courseBookingState);
      if (withNotification) {
         notifyResult2Consumers(courseBookingEndResult, courseBookingState);
      }
      return courseBookingEndResult;
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
            CourseBookingEndResult courseBookingResult = bookingStateHandler.bookCourse(userContext.id, getCurrentCourse(), state);
            notifyResult2Consumers(courseBookingResult, this.state);
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

   private void notifyResult2Consumers(CourseBookingEndResult courseBookingResult, CourseBookingState courseBookingState) {
      courseBookingEndResultConsumers.forEach(courseBookingEndResultConsumer -> courseBookingEndResultConsumer.consumeResult(ConsumerUser.of(userContext), courseBookingResult, courseBookingState));
   }

   private void pauseApp() {
      LOG.info("Handling state {} for user [{}]", PAUSED, this.userContext);
      while (isRunning) {
         try {
            Thread.sleep(STAY_IDLE_INTERVAL);
         } catch (InterruptedException e) {
            LOG.debug("Interrupted during pausing for user [{}]!", this.userContext);
            break;
         }
      }
   }

   private void handleInitializeState() {
      LOG.info("Handling state {} for user [{}]", INIT, this.userContext);
      this.initializationResult = initStateHandler.evaluateNextCourseAndState(userContext.id);
      initStateHandler.saveUpdatedWeeklyCourses(initializationResult);
      setState(initializationResult.getNextCourseBookingState());
   }

   private void handleIdleState(Duration duration2StayIdle) {
      try {
         long timeStayIdle = duration2StayIdle.toMillis();
         LOG.info("Going idle for {} for user [{}]", duration2StayIdle, this.userContext);
         while (timeStayIdle > 0) {
            Thread.sleep(Math.min(timeStayIdle, STAY_IDLE_INTERVAL));
            timeStayIdle = timeStayIdle - STAY_IDLE_INTERVAL;
         }
         LOG.info("Done idle for user [{}]", this.userContext);
         getNextState();
      } catch (InterruptedException e) {
         LOG.debug(this.getClass().getSimpleName() + " was interrupted!", e);
         // maybe we were paused externally -> don't overwrite that
         if (state != PAUSED) {
            setState(INIT);
         }
      }
   }

   Duration getDurationLeftBeforeCourseBecomesBookableSupplier() {
      long timeLeft = DateUtil.calcTimeLeftBeforeDate(getCurrentCourse().getCourseDate());
      LOG.info("getTimeLeftBeforeCourseBecomesBookableSupplier: {}ms ({}s) left for user [{}]", timeLeft, (timeLeft / 1000L), this.userContext);
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
         LOG.info("Switched from state {} to new state {} for user [{}]", this.state, newtState, this.userContext);
         this.state = newtState;
         this.courseBookingStateChangedHandlers
                 .forEach(courseBookingStateChangedHandler -> courseBookingStateChangedHandler.onCourseBookingStateChanged(this.state));
      }
   }

   private AquabasileaCourseBookerFacade getAquabasileaCourseBookerFacade(AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory, UserContext userContext) {
      return aquabasileaCourseBookerFacadeFactory.createNewAquabasileaCourseBookerFacade(userContext.username(), userContext.userPwdSupplier,
              this::getDurationLeftBeforeCourseBecomesBookableSupplier);
   }

   /**
    * {@link UserContext} is used to provide the {@link AquabasileaCourseBooker} an username and password for browser authentication
    *
    * @param id       the id
    * @param username the username
    * @param phoneNr  the phoneNr
    */
   public record UserContext(String id, String username, String phoneNr, Supplier<char[]> userPwdSupplier) {
      @Override
      public String toString() {
         return "user-id=" + id;
      }
   }
}
