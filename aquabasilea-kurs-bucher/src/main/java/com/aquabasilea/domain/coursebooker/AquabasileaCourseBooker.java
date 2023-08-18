package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.booking.facade.AquabasileaCourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.BookingStateHandler;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.domain.coursebooker.states.idle.IdleContext;
import com.aquabasilea.domain.coursebooker.states.idle.IdleStateHandler;
import com.aquabasilea.domain.coursebooker.states.idle.IdleStateResult;
import com.aquabasilea.domain.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.domain.coursebooker.states.init.InitializationResult;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.*;
import static java.util.Objects.isNull;

/**
 * The {@link AquabasileaCourseBooker} is the heart of the aquabasilea-course-booking application
 */
public class AquabasileaCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);

   private final UserContext userContext;
   private CourseBookingState state;
   private InitializationResult initializationResult;

   private final BookingStateHandler bookingStateHandler;
   private InitStateHandler initStateHandler;
   private IdleStateHandler idleStateHandler;
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
    */
   AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                           AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig,
                           AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade) {
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, aquabasileaCourseBookerFacade);
      this.userContext = userContext;
      init(aquabasileaCourseBookerConfig, weeklyCoursesRepository, courseDefRepository);
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker}
    *
    * @param userContext                          the {@link UserContext} for the specific user
    * @param weeklyCoursesRepository              the {@link WeeklyCoursesRepository} to get and store a {@link WeeklyCourses}
    * @param courseDefRepository                  the {@link CourseDefRepository} for get and store the {@link CourseDef}s
    * @param aquabasileaCourseBookerFacadeFactory the {@link AquabasileaCourseBookerFacadeFactory} in order to create an {@link AquabasileaCourseBookerFacade}
    */
   public AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                                  AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory) {
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, getAquabasileaCourseBookerFacade(aquabasileaCourseBookerFacadeFactory, userContext));
      this.userContext = userContext;
      init(new AquabasileaCourseBookerConfig(), weeklyCoursesRepository, courseDefRepository);
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, WeeklyCoursesRepository weeklyCoursesRepository,
                     CourseDefRepository courseDefRepository) {
      this.idleStateHandler = new IdleStateHandler();
      this.initStateHandler = new InitStateHandler(weeklyCoursesRepository, courseDefRepository, bookerConfig);
      this.infoString4StateEvaluator = new InfoString4StateEvaluator(bookerConfig);
      this.courseBookingStateChangedHandlers = new ArrayList<>();
      this.courseBookingEndResultConsumers = new ArrayList<>();
      setState(PAUSED);
   }

   public void start() {
      setState(INIT);
      LOG.info("AquabasileaCourseBooker started for user [{}]", this.userContext);
   }

   public void stop() {
      LOG.info("AquabasileaCourseBooker stopped for user [{}]", this.userContext);
      setState(STOP);
   }

   /**
    * Pauses or resumes this {@link AquabasileaCourseBooker}
    * <b>Note:</b> if it is resumed, the current state is set to IDLE_BEFORE_DRY_RUN
    * regardless if the state was IDLE_BEFORE_BOOKING in the first place
    */
   public void pauseOrResume() {
      if (this.isIdle() || this.isPaused()) {
         setState(this.isIdle() ? PAUSED : INIT);
      }
   }

   /**
    * Updates the internal schedule of {@link Course}s which are going to be booked
    */
   public void refreshCourses() {
      if (this.isIdle()) {
         setState(REFRESH_COURSES);
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

   void handleCurrentState() {
      LOG.info("Handling state {} for user [{}]", this.state, this.userContext);
      switch (this.state) {
         case INIT -> handleInitializeState();
         case PAUSED -> pauseApp();
         case IDLE_BEFORE_BOOKING, IDLE_BEFORE_DRY_RUN -> handleIdleState();
         case BOOKING, BOOKING_DRY_RUN -> {
            CourseBookingEndResult courseBookingResult = bookingStateHandler.bookCourse(userContext.id, getCurrentCourse(), state);
            notifyResult2Consumers(courseBookingResult, this.state);
            getNextState();
         }
         default -> throw new IllegalStateException("Unhandled state '" + this.state + "'");
      }
   }

   private void notifyResult2Consumers(CourseBookingEndResult courseBookingResult, CourseBookingState courseBookingState) {
      courseBookingEndResultConsumers.forEach(courseBookingEndResultConsumer -> courseBookingEndResultConsumer.consumeResult(ConsumerUser.of(userContext), courseBookingResult, courseBookingState));
   }

   private void pauseApp() {
      IdleContext idleContext = IdleContext.isPaused(userContext);
      IdleStateResult idleStateResult = idleStateHandler.handleIdleState(idleContext);
      setState(idleStateResult.nextState());
   }

   private void handleInitializeState() {
      this.initializationResult = initStateHandler.evaluateNextCourseAndState(userContext.id);
      initStateHandler.saveUpdatedWeeklyCourses(initializationResult);
      setState(initializationResult.getNextCourseBookingState());
   }
   private void handleIdleState() {
      IdleContext idleContext = IdleContext.of(initializationResult.getDurationUtilDryRunOrBookingBegin(), state, userContext);
      IdleStateResult idleStateResult = idleStateHandler.handleIdleState(idleContext);
      // is we are paused, don't change it
      if (state != PAUSED) {
         setState(idleStateResult.nextState());
      }
   }

   Duration getDurationLeftBeforeCourseBecomesBookableSupplier() {
      long timeLeft = DateUtil.calcTimeLeftBeforeDate(getCurrentCourse().getCourseDate());
      LOG.info("getTimeLeftBeforeCourseBecomesBookableSupplier: {}ms ({}s) left for user [{}]", timeLeft, (timeLeft / 1000L), this.userContext);
      return Duration.ofMillis(timeLeft);
   }

   private void getNextState() {
      setState(state.next());
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
      return idleStateHandler.isPausing();
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
