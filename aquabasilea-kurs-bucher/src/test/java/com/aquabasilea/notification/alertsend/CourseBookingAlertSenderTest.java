package com.aquabasilea.notification.alertsend;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.notification.alertsend.config.AlertSendConfigProviderImpl;
import com.brugalibre.notification.api.v1.model.AlertSendResponse;
import com.brugalibre.notification.api.v1.service.AlertSendException;
import com.brugalibre.notification.api.v1.service.AlertSendService;
import com.brugalibre.notification.config.AlertSendConfig;
import com.brugalibre.notification.send.common.model.AlertSendInfos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CourseBookingAlertSenderTest {

   public static final String ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML = "alert/test-aquabasilea-alert-notification.yml";
   public static final AlertSendConfigProviderImpl CONFIG_PROVIDER = new AlertSendConfigProviderImpl(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML,
           "test-aquabasilea-alert.keystore", "test-aquabasilea-keystore.keystore");
   private static final ConsumerUser CONSUMER_USER = new ConsumerUser("1234", "1234");

   @Test
   void consumeAndSendSmsBookingSuccessful() {
      // Given
      String courseName = "courseName";
      String apiKey = "abc-123-apikey";
      String expectedTitle = TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.COURSE_SUCCESSFULLY_BOOKED, courseName)
              + "\n\n"
              + String.format(TextResources.SMS_TEXT_CANCEL_BOOKED_COURSE, TextResources.CANCEL_BOOKED_COURSE_SMS_CODE, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      TestAlertSendService alertSendService = spy(new TestAlertSendService());
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
      assertThat(alertSendService.apiKey, is(apiKey));
   }

   @Test
   void consumeAndSendSmsBookingFailed_NotBookable() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_TECHNICAL_ERROR, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsBookingFailed_NotBookableFullyBooked() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE_FULLY_BOOKED, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_COURSE_FULLY_BOOKED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsBookingFailed_Aborted() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.COURSE_BOOKING_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsDryRunFailed_Aborted() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_DRY_RUN_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.COURSE_DRY_RUN_SKIPPED_COURSE_NO_COURSE_DEF, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsBookingNoSmsSend_InvalidClickResult() throws AlertSendException {
      // Given
      String courseName = "courseName";
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_FAILED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService, never()).sendAlert(any(), any());
   }

   @Test
   void consumeAndSendSmsBookingFailedNPE() throws AlertSendException {
      // Given
      String exceptionDetailMsg = "hoppla da hats getschädderet";
      NullPointerException exception = new NullPointerException(exceptionDetailMsg);
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_BOOKING_RESULTS.formatted(courseName);
      String exceptionMsg = exception.getClass().getSimpleName() + ":\n" + exceptionDetailMsg;
      String expectedMsg = String.format(TextResources.COURSE_NOT_BOOKABLE_EXCEPTION, courseName, exceptionMsg);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED, courseName, exceptionMsg);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsBookingFailedWithException_ButNoActualException() throws AlertSendException {
      // Given
      String courseName = "courseName";
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING);

      // Then
      verify(alertSendService).sendAlert(any(), any());
   }

   @Test
   void consumeAndSendSmsDryRunSuccessful_Aborted() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_DRY_RUN_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.DRY_RUN_FINISHED_SUCCESSFULLY, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_SUCCESSFUL, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendSmsDryRunFailed_CourseFullyBooked() throws AlertSendException {
      // Given, dry-run with invalid book-result
      String courseName = "courseName";
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_COURSE_FULLY_BOOKED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService, never()).sendAlert(any(), any());
   }

   @Test
   void consumeAndSendSmsDryRunFailed_Exception() throws AlertSendException {
      // Given
      String courseName = "courseName";
      String expectedTitle = TextResources.COURSE_DRY_RUN_RESULTS.formatted(courseName);
      String expectedMsg = String.format(TextResources.DRY_RUN_FINISHED_FAILED, courseName);
      AlertSendInfos expectedAlertSendInfos = new AlertSendInfos(expectedTitle, expectedMsg, List.of(CONSUMER_USER.phoneNr()));
      AlertSendService alertSendService = mock(AlertSendService.class);
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED, courseName);

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      verify(alertSendService).sendAlert(any(), eq(expectedAlertSendInfos));
   }

   @Test
   void consumeAndSendErrorDuringSending() {
      // Given
      AtomicBoolean wasThrown = new AtomicBoolean();
      AlertSendService alertSendService = (config, text) -> {
         wasThrown.set(true);
         throw new AlertSendException(new NullPointerException("Hoppla"));
      };
      CourseBookingEndResultConsumer courseBookingEndResultConsumer = new CourseBookingAlertSender(CONFIG_PROVIDER, conf -> alertSendService);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, "asdf");

      // When
      courseBookingEndResultConsumer.consumeResult(CONSUMER_USER, courseBookingResultDetails, CourseBookingState.BOOKING_DRY_RUN);

      // Then
      assertThat(wasThrown.get(), is(true));
   }

   private static class TestAlertSendService implements AlertSendService {

      String apiKey;

      @Override
      public AlertSendResponse sendAlert(AlertSendConfig alertSendConfig, AlertSendInfos alertSendInfos) {
         this.apiKey = alertSendConfig.getApiKey();
         return new TestAlertResponse(200, "OK");
      }
   }
}