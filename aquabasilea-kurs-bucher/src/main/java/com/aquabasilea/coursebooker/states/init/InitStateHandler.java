package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.aquabasilea.coursebooker.states.CourseBookingState.IDLE_BEFORE_BOOKING;
import static com.aquabasilea.coursebooker.states.CourseBookingState.IDLE_BEFORE_DRY_RUN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Contains the logic for handling the states {@link CourseBookingState#IDLE_BEFORE_BOOKING} and {@link CourseBookingState#IDLE_BEFORE_DRY_RUN}
 */
 public class InitStateHandler {
   private final static Logger LOG = LoggerFactory.getLogger(InitStateHandler.class);
   private final String weeklyCoursesYmlFile;
   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;

   public InitStateHandler(String weeklyCoursesYmlFile, AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      this.weeklyCoursesYmlFile = weeklyCoursesYmlFile;
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig;
   }

   /**
    * Evaluates the next course and also, if there will be a dry run for this course
    * or if the dry-run is skipped and directly moved to the {@link CourseBookingState#BOOKING}
    * If there is no next corse, {@link CourseBookingState#STOP} is returned as a default result
    *
    * @return an {@link InitializationResult} containing the next {@link Course} as well as the next {@link CourseBookingState}
    */
   public InitializationResult evaluateNextCourseAndState() {
      aquabasileaCourseBookerConfig.refresh();
      WeeklyCourses weeklyCourses = WeeklyCourses.readWeeklyCourses(weeklyCoursesYmlFile);
      return getCourseAndTimeUntilStart(weeklyCourses);
   }

   private InitializationResult getCourseAndTimeUntilStart(WeeklyCourses weeklyCourses) {
      LocalDateTime refDate = LocalDateTime.now();
      InitializationResult idleBeforeBookingResult = checkAllCoursesAndGetEarliestCourseAndTimeUntilStart(weeklyCourses, refDate);
      if (isNull(idleBeforeBookingResult)) {
         LOG.warn("No courses defined!");
         return InitializationResult.stop();
      }
      InitializationResult idleBeforeDryRunResult = getDryRunInitializationResult(refDate, idleBeforeBookingResult);
      if (nonNull(idleBeforeDryRunResult)) {
         LOG.info("Found next course '{}' for dry run. Starting {}s earlier", idleBeforeDryRunResult.getCurrentCourse(), idleBeforeDryRunResult.getTimeUtilDryRunOrBookingBegin());
         return idleBeforeDryRunResult;
      }
      LOG.info("Found next course '{}' for booking. Starting {}s earlier", idleBeforeBookingResult.getCurrentCourse(), idleBeforeBookingResult.getTimeUtilDryRunOrBookingBegin());
      return idleBeforeBookingResult;
   }

   /**
    * Tries to find the next course. Normally, the booker starts one day and some minutes before the courses is scheduled (since
    * a course is closed for booking until 24h before)
    *
    * From all found Courses which may match, we take the one which is the closest from now
    */
   private InitializationResult checkAllCoursesAndGetEarliestCourseAndTimeUntilStart(WeeklyCourses weeklyCourses, LocalDateTime refDate) {
      List<InitializationResult> possibleCourses = new ArrayList<>();
      for (Course course : weeklyCourses.getCourses()) {
         InitializationResult initializationResult = getCourseAndTimeUntilStart(course, refDate);
         if (nonNull(initializationResult)) {
            possibleCourses.add(initializationResult);
         }
      }
      return possibleCourses.stream()
              .min(Comparator.comparing(InitializationResult::getTimeUtilDryRunOrBookingBegin))
              .orElseGet(() -> shiftCourseDateAWeekAheadAndTryAgain(weeklyCourses, refDate));
   }

   /**
    * If we have bookable/non-paused courses but non has been found, then we are obviously too late to book them. Meaning:
    * The course date of all course is less than 24h in the future -> shift this course for one week into the future
    * and try again
    */
   private InitializationResult shiftCourseDateAWeekAheadAndTryAgain(WeeklyCourses weeklyCourses, LocalDateTime refDate) {
      if (!weeklyCourses.getCourses().isEmpty()) {
         weeklyCourses.shiftCourseDateByDays(7);
         return checkAllCoursesAndGetEarliestCourseAndTimeUntilStart(weeklyCourses, refDate);
      }
      return null;
   }

   /**
    * If there is no time left for a dry run, on the very same day the booking is running, then we skip the dry-run
    * If the user starts the app on such a short notice, then he can watch it himself
    */
   private InitializationResult getDryRunInitializationResult(LocalDateTime refDate, InitializationResult idleBeforeBookingResult) {
      long time2Sleep = getTime2Sleep(IDLE_BEFORE_DRY_RUN, idleBeforeBookingResult.getCurrentCourse(), refDate, idleBeforeBookingResult.getAmountOfDaysPrior());
      if (time2Sleep > 0) {
         return new InitializationResult(idleBeforeBookingResult.getCurrentCourse(), time2Sleep, IDLE_BEFORE_DRY_RUN, idleBeforeBookingResult.getAmountOfDaysPrior());
      }
      return null;
   }

   private InitializationResult getCourseAndTimeUntilStart(Course course, LocalDateTime refDate) {
      int daysOffset = aquabasileaCourseBookerConfig.getDaysToBookCourseEarlier();
      long time2Sleep = getTime2Sleep(IDLE_BEFORE_BOOKING, course, refDate, daysOffset);
      if (time2Sleep > 0) {
         return new InitializationResult(course, time2Sleep, IDLE_BEFORE_BOOKING, daysOffset);
      }
      return null;
   }

   private long getTime2Sleep(CourseBookingState nextCourseBookingState, Course course, LocalDateTime refDate, int daysOffset) {
      Duration durationToStartEarlier = nextCourseBookingState == IDLE_BEFORE_BOOKING ?
              aquabasileaCourseBookerConfig.getDurationToStartBookerEarlier() : aquabasileaCourseBookerConfig.getDurationToStartDryRunEarlier();
      LocalDateTime courseDateMinusOffset = course.getCourseDate().minusDays(daysOffset);
      return DateUtil.getMillis(courseDateMinusOffset)
              - DateUtil.getMillis(refDate)
              - durationToStartEarlier.toMillis();
   }
}
