package com.aquabasilea.domain.coursebooker.states.init;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.util.DateUtil;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.IDLE_BEFORE_BOOKING;
import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.IDLE_BEFORE_DRY_RUN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;

/**
 * Contains the logic for handling the states {@link CourseBookingState#IDLE_BEFORE_BOOKING} and {@link CourseBookingState#IDLE_BEFORE_DRY_RUN}
 */
public class InitStateHandler {
   private final static Logger LOG = LoggerFactory.getLogger(InitStateHandler.class);

   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;
   private final DelayHelper delayHelper;

   public InitStateHandler(WeeklyCoursesRepository weeklyCoursesRepository,
                           AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig, Duration maxDelay) {
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.delayHelper = new DelayHelper(maxDelay);
   }

   /**
    * Evaluates the next course and also, if there will be a dry run for this course
    * or if the dry-run is skipped and directly moved to the {@link CourseBookingState#BOOKING}
    * If there are no next courses, {@link CourseBookingState#PAUSED} is returned as a default result
    *
    * @param userId the id of the {@link User} for which the next {@link Course} is evaluated
    * @return an {@link InitializationResult} containing the next {@link Course} as well as the next {@link CourseBookingState}
    */
   public InitializationResult evaluateNextCourseAndState(String userId) {
      aquabasileaCourseBookerConfig.refresh();
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      return getCourseAndTimeUntilStart(weeklyCourses);
   }

   private InitializationResult getCourseAndTimeUntilStart(WeeklyCourses weeklyCourses) {
      LocalDateTime refDate = LocalDateTime.now();
      InitializationResult idleBeforeBookingResult = checkAllCoursesAndGetEarliestCourseAndTimeUntilStart(weeklyCourses, refDate);
      if (isNull(idleBeforeBookingResult)) {
         LOG.warn("No courses defined!");
         return InitializationResult.pause(weeklyCourses);
      }
      InitializationResult idleBeforeDryRunResult = getDryRunInitializationResult(refDate, idleBeforeBookingResult);
      if (nonNull(idleBeforeDryRunResult)) {
         LOG.info("Found next course [{}] for dry run. Starting {}s earlier.", idleBeforeDryRunResult.getCurrentCourse(), idleBeforeDryRunResult.getDurationUtilDryRunOrBookingBegin());
         return idleBeforeDryRunResult;
      }
      LOG.info("Found next course [{}] for booking. Starting {}s earlier.", idleBeforeBookingResult.getCurrentCourse(), idleBeforeBookingResult.getDurationUtilDryRunOrBookingBegin());
      return idleBeforeBookingResult;
   }

   /**
    * Tries to find the next course. Normally, the booker starts one day and some minutes before the courses is scheduled (since
    * a course is closed for booking until 24h before)
    * <p>
    * From all found Courses which may match, we take the one which is the closest from now
    */
   private InitializationResult checkAllCoursesAndGetEarliestCourseAndTimeUntilStart(WeeklyCourses weeklyCourses, LocalDateTime refDate) {
      List<InitializationResult> possibleCourses = new ArrayList<>();
      for (Course course : getNonPausedCourses(weeklyCourses)) {
         InitializationResult initializationResult = getCourseAndTimeUntilStart(course, refDate, weeklyCourses);
         if (isNull(initializationResult)) {
            LOG.info("Shift course {} (id={}) 7 days into the future", course.getCourseName(), course.getId());
            course.shiftCourseDateByDays(7);
            initializationResult = getCourseAndTimeUntilStart(course, refDate, weeklyCourses);
         }
         possibleCourses.add(initializationResult);
      }
      return possibleCourses.stream()
              .filter(Objects::nonNull)
              .min(Comparator.comparing(InitializationResult::getDurationUtilDryRunOrBookingBegin))
              .orElse(null);
   }

   /**
    * If there is no time left for a dry run, on the very same day the booking is running, then we skip the dry-run
    * If the user starts the app on such a short notice, then he can watch it himself
    */
   private InitializationResult getDryRunInitializationResult(LocalDateTime refDate, InitializationResult idleBeforeBookingResult) {
      long time2Sleep = getTime2Sleep(IDLE_BEFORE_DRY_RUN, idleBeforeBookingResult.getCurrentCourse(), refDate, idleBeforeBookingResult.getAmountOfDaysPrior());
      if (time2Sleep > 0) {
         return InitializationResult.dryRunInitializationResult(idleBeforeBookingResult, time2Sleep);
      }
      return null;
   }

   private InitializationResult getCourseAndTimeUntilStart(Course course, LocalDateTime refDate, WeeklyCourses weeklyCourses) {
      int daysOffset = aquabasileaCourseBookerConfig.getDaysToBookCourseEarlier();
      long time2Sleep = getTime2Sleep(IDLE_BEFORE_BOOKING, course, refDate, daysOffset);
      if (time2Sleep > 0) {
         return InitializationResult.bookingInitializationResult(course, time2Sleep, daysOffset, weeklyCourses);
      }
      LOG.info("getCourseAndTimeUntilStart: negative time to sleep {}, refDate={}, courseDate={}", time2Sleep, refDate, course.getCourseDate());
      return null;
   }

   private long getTime2Sleep(CourseBookingState nextCourseBookingState, Course course, LocalDateTime refDate, int daysOffset) {
      Duration durationToStartEarlier = nextCourseBookingState == IDLE_BEFORE_BOOKING ?
              aquabasileaCourseBookerConfig.getDurationToStartBookerEarlier() : aquabasileaCourseBookerConfig.getDurationToStartDryRunEarlier();
      durationToStartEarlier = durationToStartEarlier.plusSeconds(delayHelper.getRandomDelay());
      LocalDateTime courseDateMinusOffset = course.getCourseDate().minusDays(daysOffset);
      return DateUtil.getMillis(courseDateMinusOffset)
              - DateUtil.getMillis(refDate)
              - durationToStartEarlier.toMillis();
   }

   private static List<Course> getNonPausedCourses(WeeklyCourses weeklyCourses) {
      return weeklyCourses.getCourses()
              .stream()
              .filter(not(Course::getIsPaused))
              .collect(Collectors.toList());
   }
}
