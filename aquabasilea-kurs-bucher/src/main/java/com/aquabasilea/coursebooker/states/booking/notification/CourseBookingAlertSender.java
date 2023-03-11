package com.aquabasilea.coursebooker.states.booking.notification;

import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.brugalibre.notification.api.AlertSendService;
import com.brugalibre.notification.config.AlertSendConfig;
import com.brugalibre.notification.config.AlertSendConfigProvider;
import com.brugalibre.notification.send.AlertSendInfos;
import com.brugalibre.notification.send.BasicAlertSender;
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
   public void consumeResult(ConsumerUser consumerUser, CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      String msg = getMessage4Result(courseBookingEndResult, courseBookingState);
      if (nonNull(msg)) {
         AlertSendInfos alertSendInfos = new AlertSendInfos(msg, List.of(consumerUser.phoneNr()));
         sendMessage(alertSendInfos);
      }
   }

   private String getMessage4Result(CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      String courseName = courseBookingEndResult.getCourseName();

      switch (courseBookingState) {
         case BOOKING:
            return getMessage4ResultBooked(courseBookingEndResult, courseName);
         case BOOKING_DRY_RUN:
            return getMessage4ResultDryRun(courseBookingEndResult, courseName);
         default:
            LOG.error("Warning! Unhandled state [{}]", courseBookingState);
            return null;
      }
   }

   private static String getMessage4ResultDryRun(CourseBookingEndResult courseBookingEndResult, String courseName) {
      switch (courseBookingEndResult.getCourseClickedResult()) {
         case COURSE_NOT_SELECTED_NO_SINGLE_RESULT: // fall through
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED: // fall through
            return String.format(TextResources.DRY_RUN_FINISHED_FAILED, courseName);
         case COURSE_BOOKING_ABORTED:
            return String.format(TextResources.DRY_RUN_FINISHED_SUCCESSFULLY, courseName);
         case COURSE_BOOKING_SKIPPED:
            return String.format(TextResources.COURSE_DRY_RUN_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
         default:
            LOG.error("Warning! Unhandled state [{}]", courseBookingEndResult.getCourseClickedResult());
            return null;
      }
   }

   private static String getMessage4ResultBooked(CourseBookingEndResult courseBookingEndResult, String courseName) {
      switch (courseBookingEndResult.getCourseClickedResult()) {
         case COURSE_BOOKED:
            return String.format(TextResources.COURSE_SUCCESSFULLY_BOOKED, courseName);
         case COURSE_NOT_BOOKABLE:
            return String.format(TextResources.COURSE_NOT_BOOKABLE, courseName);
         case COURSE_NOT_BOOKABLE_FULLY_BOOKED:
            return String.format(TextResources.COURSE_NOT_BOOKABLE_FULLY_BOOKED, courseName);
         case COURSE_NOT_SELECTED_NO_SINGLE_RESULT:
            return String.format(TextResources.COURSE_NOT_BOOKABLE_NO_SINGLE_RESULT, courseName);
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED:
            String exceptionMsg = getExceptionMsg(courseBookingEndResult);
            return String.format(TextResources.COURSE_NOT_BOOKABLE_EXCEPTION, courseName, exceptionMsg);
         case COURSE_BOOKING_SKIPPED:
            return String.format(TextResources.COURSE_BOOKING_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
         default:
            LOG.error("Warning! Unhandled state [{}]", courseBookingEndResult.getCourseClickedResult());
            return null;
      }
   }

   private static String getExceptionMsg(CourseBookingEndResult courseBookingEndResult) {
      Exception exception = courseBookingEndResult.getException();
      return exception.getClass().getSimpleName() + ":\n" + exception.getMessage();
   }
}
