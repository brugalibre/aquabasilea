package com.aquabasilea.notification.alertsend;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.brugalibre.notification.api.v1.service.AlertSendService;
import com.brugalibre.notification.config.AlertSendConfig;
import com.brugalibre.notification.config.AlertSendConfigProvider;
import com.brugalibre.notification.send.common.model.AlertSendInfos;
import com.brugalibre.notification.send.common.service.BasicAlertSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

/**
 * The {@link CourseBookingAlertSender} sends an alert configured by a {@link AlertSendConfig} to one or more subscribers
 */
public class CourseBookingAlertSender extends BasicAlertSender implements CourseBookingEndResultConsumer {

   private static final Logger LOG = LoggerFactory.getLogger(CourseBookingAlertSender.class);

   /**
    * Constructor only for testing purpose
    *
    * @param configProvider a {@link Supplier} which provides the {@link AlertSendConfig}
    */
   public CourseBookingAlertSender(AlertSendConfigProvider configProvider, Function<AlertSendConfig, AlertSendService> alertServiceFunction) {
      super(configProvider, alertServiceFunction);
   }

   public CourseBookingAlertSender(AlertSendConfigProvider configProvider) {
      super(configProvider);
   }

   @Override
   public void consumeResult(ConsumerUser consumerUser, CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState) {
      String msg = getMessage4Result(courseBookingResultDetails, courseBookingState);
      if (nonNull(msg)) {
         String title = getTitle4Result(courseBookingResultDetails, courseBookingState);
         AlertSendInfos alertSendInfos = new AlertSendInfos(title, msg, List.of(consumerUser.phoneNr()));
         sendMessage(alertSendInfos);
      }
   }

   private String getMessage4Result(CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState) {
      String courseName = courseBookingResultDetails.getCourseName();
      switch (courseBookingState) {
         case BOOKING:
            return getMessage4ResultBooked(courseBookingResultDetails, courseName);
         case BOOKING_DRY_RUN:
            return getMessage4ResultDryRun(courseBookingResultDetails, courseName);
         default:
            LOG.error("Warning! getMessage4Result: Unhandled state '{}'", courseBookingState);
            return null;
      }
   }

   private String getTitle4Result(CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState) {
      String courseName = courseBookingResultDetails.getCourseName();
      switch (courseBookingState) {
         case BOOKING:
            return TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
         case BOOKING_DRY_RUN:
            return TextResources.COURSE_DRY_RUN_RESULTS.formatted(courseName);
         default:
            LOG.error("Warning! getMessage4Result: Unhandled state '{}'", courseBookingState);
            return null;
      }
   }

   private static String getMessage4ResultDryRun(CourseBookingResultDetails courseBookingResultDetails, String courseName) {
      switch (courseBookingResultDetails.getCourseBookResult()) {
         case NOT_BOOKED_EXCEPTION_OCCURRED: // fall through
         case DRY_RUN_FAILED: // fall through
            return String.format(TextResources.DRY_RUN_FINISHED_FAILED, courseName);
         case DRY_RUN_SUCCESSFUL:
            return String.format(TextResources.DRY_RUN_FINISHED_SUCCESSFULLY, courseName);
         case BOOKING_SKIPPED:
            return String.format(TextResources.COURSE_DRY_RUN_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
         default:
            LOG.error("Warning! getMessage4ResultDryRun: Unhandled state '{}'", courseBookingResultDetails.getCourseBookResult());
            return null;
      }
   }

   private static String getMessage4ResultBooked(CourseBookingResultDetails courseBookingResultDetails, String courseName) {
      switch (courseBookingResultDetails.getCourseBookResult()) {
         case BOOKED:
            return getSuccessfullyBookedMessageText(courseName);
         case NOT_BOOKED_COURSE_ALREADY_BOOKED, NOT_BOOKED_TECHNICAL_ERROR:
            return String.format(TextResources.COURSE_NOT_BOOKABLE, courseName);
         case NOT_BOOKED_COURSE_FULLY_BOOKED:
            return String.format(TextResources.COURSE_NOT_BOOKABLE_FULLY_BOOKED, courseName);
         case NOT_BOOKED_EXCEPTION_OCCURRED, NOT_BOOKED_UNEXPECTED_ERROR:
            return String.format(TextResources.COURSE_NOT_BOOKABLE_EXCEPTION, courseName, courseBookingResultDetails.getErrorMessage());
         case BOOKING_SKIPPED:
            return String.format(TextResources.COURSE_BOOKING_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
         default:
            LOG.error("Warning! getMessage4ResultBooked: Unhandled state '{}'", courseBookingResultDetails.getCourseBookResult());
            return null;
      }
   }

   private static String getSuccessfullyBookedMessageText(String courseName) {
      return String.format(TextResources.COURSE_SUCCESSFULLY_BOOKED, courseName)
              + "\n\n"
              + String.format(TextResources.SMS_TEXT_CANCEL_BOOKED_COURSE, TextResources.CANCEL_BOOKED_COURSE_SMS_CODE, courseName);
   }
}
