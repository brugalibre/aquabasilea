package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.model.state.init.InitializationResult;
import com.aquabasilea.util.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

public class InfoString4StateEvaluator {

   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;

   public InfoString4StateEvaluator (AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig;
   }

   /**
    * Returns a String depending on the given {@link CourseBookingState}, the given {@link Course} and the {@link InitializationResult}
    *
    * @param courseBookingState   the current state of the {@link AquabasileaCourseBooker}
    * @param currentCourse        the current {@link Course} of the {@link AquabasileaCourseBooker}
    * @return a String depending on the given {@link CourseBookingState}, the given {@link Course} and the {@link InitializationResult}
    */
   public String getInfoString4State(CourseBookingState courseBookingState, Course currentCourse) {
      aquabasileaCourseBookerConfig.refresh();
      return switch (courseBookingState) {
         case INIT, REFRESH_COURSES -> TextResources.INFO_TEXT_INIT;
         case IDLE_BEFORE_BOOKING -> getInfoString4IdleBeforeBooking(currentCourse);
         case IDLE_BEFORE_DRY_RUN -> getInfoString4IdleBeforeDryRun(currentCourse);
         case BOOKING -> String.format(TextResources.INFO_TEXT_BOOKING_COURSE, currentCourse.getCourseName());
         case BOOKING_DRY_RUN -> String.format(TextResources.INFO_TEXT_BOOKING_COURSE_DRY_RUN, currentCourse.getCourseName());
         case PAUSED -> TextResources.INFO_TEXT_APP_PAUSED;
         case STOP -> TextResources.INFO_TEXT_APP_STOPPED;
      };
   }

   private String getInfoString4IdleBeforeBooking(Course currentCourse) {
      return getInfoString4Idle(currentCourse, aquabasileaCourseBookerConfig.getDurationToStartBookerEarlier(), TextResources.INFO_TEXT_IDLE_BEFORE_BOOKING);
   }

   private String getInfoString4IdleBeforeDryRun(Course currentCourse) {
      return getInfoString4Idle(currentCourse, aquabasileaCourseBookerConfig.getDurationToStartDryRunEarlier(), TextResources.INFO_TEXT_IDLE_BEFORE_DRY_RUN);
   }

   private String getInfoString4Idle(Course currentCourse, Duration durationToStartDryRunOrBookingEarlier, String idleBeforeDryRunOrBooking) {
      LocalDateTime courseDate = currentCourse.getCourseDate();
      LocalDateTime dryRunOrBookingDate = courseDate.minusNanos(durationToStartDryRunOrBookingEarlier.toNanos())
              .minusDays(aquabasileaCourseBookerConfig.getDaysToBookCourseEarlier());
      String courseDateAsString = DateUtil.toString(courseDate, Locale.GERMAN);
      String dryRunOrBookingDateAsString = DateUtil.toString(dryRunOrBookingDate, Locale.GERMAN);
      return String.format(idleBeforeDryRunOrBooking, currentCourse.getCourseName(), courseDateAsString, dryRunOrBookingDateAsString);
   }
}
