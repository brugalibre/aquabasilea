package com.aquabasilea.alerting.consumer.impl;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.selectcourse.result.CourseClickedResult;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AlertSenderTest {

   public static final String ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML = "alert/test-aquabasilea-alert-notification.yml";

   @Test
   void consumeAndSendSmsBookingSuccessful() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.COURSE_SUCCESSFULLY_BOOKED, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsBookingFailed_NoSingleResultSelection() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE_NO_SINGLE_RESULT, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_SELECTED_NO_SINGLE_RESULT)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsBookingFailed_NotBookable() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_BOOKABLE)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsBookingFailed_Aborted() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKED_ABORTED, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_ABORTED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsBookingNoSmsSend_InvalidClickResult() throws AlertSendException {
      // Given
      String courseName = "courseName";
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_BOOKED_RETRY)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService, never()).sendAlert(any(), any());
   }

   @Test
   void consumeAndSendSmsBookingFailedNPE() throws AlertSendException {
      // Given
      String exceptionDetailMsg = "hoppla da hats getschädderet";
      NullPointerException exception = new NullPointerException(exceptionDetailMsg);
      String courseName = "courseName";
      String exceptionMsg = exception.getClass().getSimpleName() +":\n" + exceptionDetailMsg;
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE_EXCEPTION, courseName, exceptionMsg);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withException(exception)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsDryRunSuccessful_Aborted() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.DRY_RUN_FINISHED_SUCCESSFULLY, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_ABORTED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsDryRunFailed_NoSingleSearchResult() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.DRY_RUN_FINISHED_FAILED, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_SELECTED_NO_SINGLE_RESULT)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndSendSmsDryRunNoSmsSend_InvalidClickResult() throws AlertSendException {
      // Given
      String courseName = "courseName";
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_BOOKABLE)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService, never()).sendAlert(any(), any());
   }

   @Test
   void consumeAndSendSmsDryRunFailed_Exception() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedMsg = String.format(TextResources.DRY_RUN_FINISHED_FAILED, courseName);
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedMsg));
   }

   @Test
   void consumeAndFailIllegalState() throws AlertSendException {
      // Given
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName("asdf")
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_ABORTED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.STOP);

      // Then
      verify(alertSendService, never()).sendAlert(any(), any());
   }
   @Test
   void consumeAndSendErrorDuringSending() {
      // Given
      AtomicBoolean wasThrown = new AtomicBoolean();
      AlertSendService alertSendService = (config, text) -> {
         wasThrown.set(true);
         throw new AlertSendException(new NullPointerException("Hoppla"));
      };
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new AlertSender(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, conf -> alertSendService);
      CourseBookingEndResult courseBookingEndResult = CourseBookingEndResultBuilder.builder()
              .withCourseName("asdf")
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_ABORTED)
              .build();

      // When
      courseBookingEndResultConsumer.consumeResult(courseBookingEndResult, CourseBookingState.BOOKING);

      // Then
      assertThat(wasThrown.get(), is(true));
   }
}