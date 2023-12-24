package com.aquabasilea.migrosapi.service.security.bearertoken;

import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenValidator;
import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.service.config.UrlConfig;
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

class AutoRenewBearerTokenProviderImplTest {
   private static final String HOST = "http://127.0.0.1";
   private static final String BEARER_TOKEN = "bearer-token";
   private static final String NEW_BEARER_TOKEN = "new-bearer-token";
   private static final int PORT = 8282;
   private static final String BOOK_COURSE_PATH = "/kp/api/Booking_post?";
   private static final String BOOK_COURSE_URL = HOST + ":" + PORT + BOOK_COURSE_PATH;
   private static final String DELETE_COURSE_URL = HOST + ":" + PORT + "/kp/api/Booking_delete?";
   public static final String GET_BOOKED_COURSES_PATH = "/kp/api/Booking_get?";
   private static final String GET_BOOKED_COURSES_URL = HOST + ":" + PORT + GET_BOOKED_COURSES_PATH;

   @Test
   void getBearerToken_StillValid() {
      // Given
      int ttl = 60_000;
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      AutoRenewBearerTokenProviderImpl autoRenewBearerTokenProvider = getAutoRenewBearerTokenProvider(ttl);

      String path = "/kp/api/Booking?";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse()
              .withPath(path)
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

   private static AutoRenewBearerTokenProviderImpl getAutoRenewBearerTokenProvider(int ttl) {
      BookCourseHelper bookCourseHelper = new BookCourseHelper(new UrlConfig(BOOK_COURSE_URL, DELETE_COURSE_URL, GET_BOOKED_COURSES_URL, "", ""), "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidatorImpl(bookCourseHelper, new HttpService(30));

      BearerTokenProvider bearerTokenProvider = new TestBearerTokenProvider(BEARER_TOKEN, NEW_BEARER_TOKEN);
      return new AutoRenewBearerTokenProviderImpl(bearerTokenProvider, ttl, bearerTokenValidator);
   }

   @Test
   void getBearerToken_InvalidDueToError() {
      // Given
      int ttl = 60_000;
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      AutoRenewBearerTokenProviderImpl autoRenewBearerTokenProvider = getAutoRenewBearerTokenProvider(ttl);

      String path = "/kp/api/Booking?";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse()
              .withPath(path)
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
      AutoRenewBearerTokenProviderImpl autoRenewBearerTokenProvider = getRenewBearerTokenProvider(ttl);

      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse()
              .withPath(GET_BOOKED_COURSES_PATH)
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

   private static AutoRenewBearerTokenProviderImpl getRenewBearerTokenProvider(int ttl) {
      UrlConfig urlConfig = new UrlConfig(BOOK_COURSE_URL, GET_BOOKED_COURSES_URL, "", "", "");
      BookCourseHelper bookCourseHelper = new BookCourseHelper(urlConfig, "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidatorImpl(bookCourseHelper, new HttpService(30));
      BearerTokenProvider bearerTokenProvider = new TestBearerTokenProvider(BEARER_TOKEN, NEW_BEARER_TOKEN);
      return new AutoRenewBearerTokenProviderImpl(bearerTokenProvider, ttl, bearerTokenValidator);
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