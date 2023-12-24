package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.course.service.WeeklyCoursesUpdater;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.BookingStateHandler;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.domain.coursebooker.model.state.idle.IdleContext;
import com.aquabasilea.domain.coursebooker.states.idle.IdleStateHandler;
import com.aquabasilea.domain.coursebooker.model.state.idle.IdleStateResult;
import com.aquabasilea.domain.coursebooker.states.init.InitStateHandler;
import com.aquabasilea.domain.coursebooker.model.state.init.InitializationResult;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.application.security.model.UserContext;
import com.aquabasilea.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.aquabasilea.domain.coursebooker.model.state.CourseBookingState.*;
import static java.util.Objects.isNull;

/**
 * The {@link AquabasileaCourseBooker} is the heart of the aquabasilea-course-booking application
 */
public class AquabasileaCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBooker.class);

   private final UserContext userContext;
   private CourseBookingState state;
   private InitializationResult initializationResult;

   private final CourseBookerFacade courseBookerFacade;
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
    * @param courseBookerFacadeFactory the {@link CourseBookerFacadeFactory} in order to create a
    *                                             {@link CourseDefExtractorFacade} which then implements the actual booking
    */
   public AquabasileaCourseBooker(UserContext userContext, WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository,
                                  AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig,
                                  CourseBookerFacadeFactory courseBookerFacadeFactory) {
      this.courseBookerFacade = getCourseBookerFacade(courseBookerFacadeFactory, userContext.id());
      this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, courseBookerFacade);
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
         this.weeklyCoursesUpdater.resumeAllCoursesIfAllPaused(this.userContext.id());
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
    * @return a {@link CourseBookingResultDetails} with details about the booking
    */
   public CourseBookingResultDetails bookCourse(CourseBookingState courseBookingState, String courseId, boolean withNotification) {
      CourseBookingResultDetails courseBookingResultDetails = bookingStateHandler.bookCourse(userContext.id(), courseId, courseBookingState);
      if (withNotification) {
         notifyResult2Consumers(courseBookingResultDetails, courseBookingState);
      }
      return courseBookingResultDetails;
   }

   void handleCurrentState() {
      LOG.info("Handling state '{}'", this.state);
      switch (this.state) {
         case INIT -> handleInitializeState();
         case PAUSED -> pauseApp();
         case IDLE_BEFORE_BOOKING, IDLE_BEFORE_DRY_RUN -> handleIdleState();
         case BOOKING, BOOKING_DRY_RUN -> {
            CourseBookingResultDetails courseBookingResultDetails = bookingStateHandler.bookCourse(userContext.id(), getCurrentCourse(), state);
            notifyResult2Consumers(courseBookingResultDetails, this.state);
            getNextState();
         }
         default -> throw new IllegalStateException("Unhandled state '" + this.state + "'");
      }
   }

   private void notifyResult2Consumers(CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState) {
      courseBookingEndResultConsumers.forEach(courseBookingEndResultConsumer ->
              courseBookingEndResultConsumer.consumeResult(ConsumerUser.of(userContext), courseBookingResultDetails, courseBookingState));
   }

   private void pauseApp() {
      IdleContext idleContext = IdleContext.getAsPaused();
      IdleStateResult idleStateResult = idleStateHandler.handleIdleState(idleContext);
      setState(idleStateResult.nextState());
   }

   private void handleInitializeState() {
      this.initializationResult = initStateHandler.evaluateNextCourseAndState(userContext.id());
      weeklyCoursesUpdater.updateCoursesHasCourseDef(initializationResult.getUpdatedWeeklyCourses());
      setState(initializationResult.getNextCourseBookingState());
   }
   private void handleIdleState() {
      IdleContext idleContext = IdleContext.of(initializationResult.getDurationUtilDryRunOrBookingBegin(), state);
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
      return courseBookerFacade.getBookedCourses();
   }

   public CourseCancelResult cancelBookedCourse(String bookingId) {
      return courseBookerFacade.cancelCourses(bookingId);
   }

   private void setState(CourseBookingState newtState) {
      if (newtState != this.state) {
         LOG.info("Switched from state {} to new state {}", this.state, newtState);
         this.state = newtState;
         this.courseBookingStateChangedHandlers
                 .forEach(courseBookingStateChangedHandler -> courseBookingStateChangedHandler.onCourseBookingStateChanged(this.state));
      }
   }

   private CourseBookerFacade getCourseBookerFacade(CourseBookerFacadeFactory courseBookerFacadeFactory, String userId) {
      return courseBookerFacadeFactory.createCourseBookerFacade(userId, this::getDurationLeftBeforeCourseBecomesBookableSupplier);
   }
}
