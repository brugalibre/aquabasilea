package com.aquabasilea.alerting.consumer.impl;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.api.factory.AlertSendServiceFactory;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.util.YamlUtil;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.aquabasilea.alerting.constants.AlertConstants.ALERT_API_CONST_FILE;
import static java.util.Objects.nonNull;

/**
 * The {@link AlertSender} sends an alert configured by a {@link AlertSendConfig} to one or more subscribers
 */
public class AlertSender implements CourseBookingEndResultConsumer {

   private static final Logger LOG = LoggerFactory.getLogger(AlertSender.class);
   private final String alertApiConstFile;
   private final Function<AlertSendConfig, AlertSendService> alertServiceFunction;

   /**
    * Constructor only for testing purpose
    *
    * @param alertApiConstFile the file with the alert config
    */
   public AlertSender(String alertApiConstFile, Function<AlertSendConfig, AlertSendService> alertServiceFunction) {
      this.alertApiConstFile = alertApiConstFile;
      this.alertServiceFunction = alertServiceFunction;
   }

   public AlertSender() {
      this.alertApiConstFile = ALERT_API_CONST_FILE;
      this.alertServiceFunction = alertSendConfig -> AlertSendServiceFactory.getAlertSendService4Name(alertSendConfig.getAlertServiceName());
   }

   @Override
   public void consumeResult(CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      AlertSendConfig alertSendConfig = YamlUtil.readYaml(alertApiConstFile, AlertSendConfig.class);
      String msg = getMessage4Result(courseBookingEndResult, courseBookingState);
      if (nonNull(msg)) {
         try {
            getAlertSendApi(alertSendConfig).sendAlert(alertSendConfig, msg);
         } catch (AlertSendException e) {
            LOG.error(String.format("Sending of alert '%s' failed!", msg), e);
         }
      }
   }

   private AlertSendService getAlertSendApi(AlertSendConfig alertSendConfig) {
      return alertServiceFunction.apply(alertSendConfig);
   }

   private String getMessage4Result(CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      String courseName = courseBookingEndResult.getCourseName();

      switch (courseBookingState) {
         case BOOKING:
            return getMessage4ResultBooked(courseBookingEndResult, courseName);
         case BOOKING_DRY_RUN:
            return getMessage4ResultDryRun(courseBookingEndResult, courseName);
         default:
            LOG.error("Warning! Unhandled state '{}'", courseBookingState);
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
         default:
            LOG.error("Warning! Unhandled state '{}'", courseBookingEndResult.getCourseClickedResult());
            return null;
      }
   }

   private static String getMessage4ResultBooked(CourseBookingEndResult courseBookingEndResult, String courseName) {
      switch (courseBookingEndResult.getCourseClickedResult()) {
         case COURSE_BOOKED:
            return String.format(TextResources.COURSE_SUCCESSFULLY_BOOKED, courseName);
         case COURSE_NOT_BOOKABLE:
            return String.format(TextResources.COURSE_NOT_BOOKABLE, courseName);
         case COURSE_NOT_SELECTED_NO_SINGLE_RESULT:
            return String.format(TextResources.COURSE_NOT_BOOKABLE_NO_SINGLE_RESULT, courseName);
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED:
            String exceptionMsg = getExceptionMsg(courseBookingEndResult);
            return String.format(TextResources.COURSE_NOT_BOOKABLE_EXCEPTION, courseName, exceptionMsg);
         case COURSE_BOOKING_ABORTED:
            return String.format(TextResources.COURSE_NOT_BOOKED_ABORTED, courseName);
         default:
            LOG.error("Warning! Unhandled state '{}'", courseBookingEndResult.getCourseClickedResult());
            return null;
      }
   }

   @NotNull
   private static String getExceptionMsg(CourseBookingEndResult courseBookingEndResult) {
      Exception exception = courseBookingEndResult.getException();
      return exception.getClass().getSimpleName() + ":\n" + exception.getMessage();
   }
}
