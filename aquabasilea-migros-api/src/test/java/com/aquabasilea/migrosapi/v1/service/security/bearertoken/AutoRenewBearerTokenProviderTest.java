package com.aquabasilea.migrosapi.v1.service.security.bearertoken;

import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.v1.service.security.BearerTokenValidator;
import com.brugalibre.common.http.auth.AuthConst;
import com.brugalibre.common.http.service.HttpService;
import com.brugalibre.test.http.DummyHttpServerTestCaseBuilder;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.service.TestRequestResponse.GET_BOOKED_COURSES_RESPONSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AutoRenewBearerTokenProviderTest {
   private static final String HOST = "http://127.0.0.1";
   private static final String BEARER_TOKEN = "bearer-token";
   private static final String NEW_BEARER_TOKEN = "new-bearer-token";
   private static final String BOOKING_PATH = "/kp/api/Booking?";
   private static final int PORT = 8282;

   @Test
   void getBearerToken_StillValid() {
      // Given
      int ttl = 60_000;
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      BookCourseHelper bookCourseHelper = new BookCourseHelper(HOST + ":" + PORT + BOOKING_PATH, "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidator(bookCourseHelper, new HttpService(30));

      BearerTokenProvider bearerTokenProvider = new TestBearerTokenProvider(BEARER_TOKEN, NEW_BEARER_TOKEN);
      AutoRenewBearerTokenProvider autoRenewBearerTokenProvider = new AutoRenewBearerTokenProvider(bearerTokenProvider, ttl, bearerTokenValidator);

      String path = "/kp/api/Booking?";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod("GET")
              .withResponseBody(GET_BOOKED_COURSES_RESPONSE)
              .withHttpStatusCode(HttpStatusCode.OK_200)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      String bearerToken = autoRenewBearerTokenProvider.getBearerToken(username, userPwdSupplier);

      // Then
      assertThat(bearerToken, is(BEARER_TOKEN));
      serverTestCaseBuilder.stop();
   }

   @Test
   void getBearerToken_InvalidDueToError() {
      // Given
      int ttl = 60_000;
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      BookCourseHelper bookCourseHelper = new BookCourseHelper(HOST + ":" + PORT + BOOKING_PATH, "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidator(bookCourseHelper, new HttpService(30));
      BearerTokenProvider bearerTokenProvider = new TestBearerTokenProvider(BEARER_TOKEN, NEW_BEARER_TOKEN);
      AutoRenewBearerTokenProvider autoRenewBearerTokenProvider = new AutoRenewBearerTokenProvider(bearerTokenProvider, ttl, bearerTokenValidator);

      String path = "/kp/api/Booking?";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod("GET")
              .withResponseBody("{}")
              .withHttpStatusCode(HttpStatusCode.OK_200)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      String bearerToken = autoRenewBearerTokenProvider.getBearerToken(username, userPwdSupplier);

      // Then, expect the same since it was not unauthorized!
      assertThat(bearerToken, is(BEARER_TOKEN));
      serverTestCaseBuilder.stop();
   }

   @Test
   void getBearerToken_InvalidDueToUnauthorized() {
      // Given
      int ttl = 60_000;
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      BookCourseHelper bookCourseHelper = new BookCourseHelper(HOST + ":" + PORT + BOOKING_PATH, "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidator(bookCourseHelper, new HttpService(30));
      BearerTokenProvider bearerTokenProvider = new TestBearerTokenProvider(BEARER_TOKEN, NEW_BEARER_TOKEN);
      AutoRenewBearerTokenProvider autoRenewBearerTokenProvider = new AutoRenewBearerTokenProvider(bearerTokenProvider, ttl, bearerTokenValidator);

      String path = "/kp/api/Booking?";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod("GET")
              .withResponseBody("")
              .withHttpStatusCode(HttpStatusCode.UNAUTHORIZED_401)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      String bearerToken = autoRenewBearerTokenProvider.getBearerToken(username, userPwdSupplier);

      // Then, expect the same since it was not unauthorized!
      assertThat(bearerToken, is(NEW_BEARER_TOKEN));
      serverTestCaseBuilder.stop();
   }

   private static class TestBearerTokenProvider implements BearerTokenProvider {
      private final String iniToken;
      private final String newToken;
      private final AtomicBoolean isCallDone = new AtomicBoolean();

      public TestBearerTokenProvider(String iniToken, String newToken) {
         this.iniToken = iniToken;
         this.newToken = newToken;
      }

      @Override
      public String getBearerToken(String username, Supplier<char[]> userPwd) {
         if (isCallDone.get()) {
            return newToken;
         }
         isCallDone.set(true);
         return iniToken;
      }
   }
}