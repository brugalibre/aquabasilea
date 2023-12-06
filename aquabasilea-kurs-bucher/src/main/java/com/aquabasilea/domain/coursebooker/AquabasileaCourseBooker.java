package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.course.service.WeeklyCoursesUpdater;
import com.aquabasilea.domain.coursebooker.booking.facade.AquabasileaCourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
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

   private final AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade;
   private final BookingStateHandler bookingStateHandler;
   private InitStateHandler initStateHandler;
   private IdleStateHandler idleStateHandler;
   private WeeklyCoursesUpdater weeklyCoursesUpdater;
   private InfoString4StateEvaluator infoString4StateEvaluator;

   private List<CourseBookingStateChangedHandler> courseBookingStateChangedHandlers;
   private List<CourseBookingEndResultConsumer> courseBookingEndResultConsumers;

   /**
    * Default constructor
    *
    * @param userContext                          the {@link UserContext}
    * @param weeklyCoursesRepository              the {@link WeeklyCoursesRepository}
    * @param courseDefRepository                  the {@link CourseDefRepository}
    * @param aquabasileaCourseBookerConfig        the {@link AquabasileaCourseBookerConfig}
    * @param aquabasileaCourseBookerFacadeFactory the {@link AquabasileaCourseBookerFacadeFactory} in order to create a
    *                                             {@link AquabasileaCourseBookerFacade} which then implements the actual booking
    */
   public AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                                  AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig,
                                  AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory) {
      this.aquabasileaCourseBookerFacade = getAquabasileaCourseBookerFacade(aquabasileaCourseBookerFacadeFactory, userContext);
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, aquabasileaCourseBookerFacade);
      this.userContext = userContext;
      init(aquabasileaCourseBookerConfig, weeklyCoursesRepository, courseDefRepository, aquabasileaCourseBookerConfig.getMaxBookerStartDelay());
   }

   private void init(AquabasileaCourseBookerConfig bookerConfig, WeeklyCoursesRepository weeklyCoursesRepository,
                     CourseDefRepository courseDefRepository, Duration maxDelay) {
      this.idleStateHandler = new IdleStateHandler();
      this.initStateHandler = new InitStateHandler(weeklyCoursesRepository, bookerConfig, maxDelay);
      this.weeklyCoursesUpdater = new WeeklyCoursesUpdater(weeklyCoursesRepository, courseDefRepository);
      this.infoString4StateEvaluator = new InfoString4StateEvaluator(bookerConfig);
      this.courseBookingStateChangedHandlers = new ArrayList<>();
      this.courseBookingEndResultConsumers = new ArrayList<>();
      setState(PAUSED);
   }

   public void start() {
      setState(INIT);
      LOG.info("AquabasileaCourseBooker started");
   }

   public void stop() {
      LOG.info("AquabasileaCourseBooker stopped");
      setState(STOP);
   }

   /**
    * Pauses or resumes the {@link AquabasileaCourseBooker} which belongs to the given user-id.
    * If currently all courses are paused, then all courses are resumed as well.
    * <b>Note:</b> this only takes effect if <b>all</b> courses are paused. So if there exists one un-paused course,
    * only the {@link AquabasileaCourseBooker} is resumed
    *
    */
   public void pauseOrResume() {
      if (isPaused()) {
         this.weeklyCoursesUpdater.resumeAllCoursesIfAllPaused(this.userContext.id);
      }
      if (isIdle() || isPaused()) {
         setState(isIdle() ? PAUSED : INIT);
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
      LOG.info("Handling state '{}'", this.state);
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
      weeklyCoursesUpdater.updateCoursesHasCourseDef(initializationResult.getUpdatedWeeklyCourses());
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
      LOG.info("getTimeLeftBeforeCourseBecomesBookableSupplier: {}ms ({}s) left", timeLeft, (timeLeft / 1000L));
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

   public List<Course> getBookedCourses() {
      return aquabasileaCourseBookerFacade.getBookedCourses();
   }

   public CourseCancelResult cancelBookedCourse(String bookingId) {
      return aquabasileaCourseBookerFacade.cancelCourses(bookingId);
   }

   private void setState(CourseBookingState newtState) {
      if (newtState != this.state) {
         LOG.info("Switched from state {} to new state {}", this.state, newtState);
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
    * @param id              the id
    * @param username        the username
    * @param phoneNr         the phoneNr
    * @param userPwdSupplier a {@link Supplier} which always provides a fresh instance of the password, even if it has been changed
    */
   public record UserContext(String id, String username, String phoneNr, Supplier<char[]> userPwdSupplier) {
      @Override
      public String toString() {
         return "user-id=" + id;
      }
   }
}
