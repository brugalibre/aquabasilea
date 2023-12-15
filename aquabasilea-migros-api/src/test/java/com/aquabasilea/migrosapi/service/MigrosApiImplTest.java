package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.v1.model.book.response.CourseCancelResult;
import com.aquabasilea.migrosapi.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosCourse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.brugalibre.common.http.auth.AuthConst;
import com.brugalibre.common.http.model.method.HttpMethod;
import com.brugalibre.common.http.service.HttpService;
import com.brugalibre.test.http.DummyHttpServerTestCaseBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.service.TestRequestResponse.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MigrosApiImplTest {

   private static final int PORT = 8282;
   private static final String GET_COURSES_PATH = "/kp/api/Courselist/all?";
   private static final String BOOK_COURSE_PATH = "/kp/api/Booking?";
   private static final String HOST = "http://127.0.0.1";
   private static final String BEARER_TOKEN = "bearer-token";
   private static final BearerTokenProvider BEARER_TOKEN_PROVIDER = (username, pwd) -> BEARER_TOKEN;
   private static final BearerTokenProvider NULL_BEARER_TOKEN_PROVIDER = (username, pwd) -> null;
   public static final HttpService HTTP_SERVICE = new HttpService(30);

   @Test
   void getCourses() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl("", HOST + ":" + PORT + GET_COURSES_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withResponseBody(GET_COURSES_RESPONSE)
              .withRequestBody(GET_COURSES_REQUEST)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .build();

      // When
      MigrosApiGetCoursesResponse coursesResponse = migrosApi.getCourses(MigrosApiGetCoursesRequest.of(List.of("129", "139")));

      // Then
      serverTestCaseBuilder.stop();
      assertThat(coursesResponse.courses().size(), is(2));
      Optional<MigrosCourse> course1Opt = findCourse4Name(coursesResponse.courses(), COURSE_NAME_1);
      assertThat(course1Opt.isPresent(), is(true));
      assertThat(course1Opt.get().centerId(), is(CENTER_ID_1));
      Optional<MigrosCourse> course2Opt = findCourse4Name(coursesResponse.courses(), COURSE_NAME_2);
      assertThat(course2Opt.isPresent(), is(true));
      assertThat(course2Opt.get().centerId(), is(CENTER_ID_2));
   }

   @Test
   void getBookedCourses() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, "", BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      Supplier<char[]> userPwdSupplier = ""::toCharArray;
      String username = "peter";
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("GET")
              .withResponseBody(GET_BOOKED_COURSES_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      MigrosApiGetBookedCoursesResponse migrosApiGetBookedCoursesResponse = migrosApi.getBookedCourses(new AuthenticationContainer(username, userPwdSupplier));

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiGetBookedCoursesResponse.courses().size(), is(2));
      Optional<MigrosCourse> course1Opt = findCourse4Name(migrosApiGetBookedCoursesResponse.courses(), COURSE_NAME_1);
      assertThat(course1Opt.isPresent(), is(true));
      assertThat(course1Opt.get().centerId(), is(CENTER_ID_2));
      Optional<MigrosCourse> course2Opt = findCourse4Name(migrosApiGetBookedCoursesResponse.courses(), COURSE_NAME_2);
      assertThat(course2Opt.isPresent(), is(true));
      assertThat(course2Opt.get().centerId(), is(CENTER_ID_1));
   }

   @Test
   void bookCourse() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(BOOK_COURSE_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      String username = "username";

      Supplier<char[]> userPwd = "pasd"::toCharArray;
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(5);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(false, durationSupplier));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.errorMsg(), is(nullValue()));
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_BOOKED));
   }

   @Test
   void bookCourseFailed_CourseFullyBooked() {
      // Given
      String username = "username";
      Supplier<char[]> userPwd = "pasd"::toCharArray;
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(BOOK_COURSE_FAILED_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(false, () -> Duration.ofMillis(1)));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.errorMsg(), is(TECHNISCHES_PROBLEM_1507));
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED));
   }

   @Test
   void bookCourseFailed_ExceptionDuringBooking() {
      // Given
      String username = "username";
      Supplier<char[]> userPwd = "pasd"::toCharArray;
      String expectedErrorMsg = "Unexpected end-of-input: expected close marker for Object (start marker at [Source: (String)\" {\";";
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      String invalidResponseBody = " {";// invalid response body in order to get an exception
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(invalidResponseBody)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(5);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(false, durationSupplier));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.errorMsg(), CoreMatchers.startsWith(expectedErrorMsg));
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED));
   }

   @Test
   void bookCourse_WithoutBearerToken() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, NULL_BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody("")
              .withHttpStatusCode(HttpStatusCode.UNAUTHORIZED_401)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .build();

      // When
      String username = "username";

      Supplier<char[]> userPwd = "pasd"::toCharArray;
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(5);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(false, durationSupplier));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_NOT_BOOKED_UNEXPECTED_ERROR));
   }

   @Test
   void bookCourseDryRunSuccessful() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(BOOK_COURSE_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      String username = "username";

      Supplier<char[]> userPwd = "pasd"::toCharArray;
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      Supplier<Duration> durationSupplier = () -> Duration.ofHours(5);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(true, durationSupplier));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.errorMsg(), is(""));
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_BOOKING_DRY_RUN_SUCCESSFUL));
   }

   @Test
   void bookCourseDryRunFailed_NoBearerToken() {
      // Given
      String username = "username";
      Supplier<char[]> userPwd = "pasd"::toCharArray;
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      Supplier<Duration> durationSupplier = () -> Duration.ofHours(1);

      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + GET_COURSES_PATH, NULL_BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(GET_COURSES_PATH)
              .withMethod("POST")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(BOOK_COURSE_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(COURSE_NAME_1, "7", "139", new MigrosBookContext(true, durationSupplier));
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.errorMsg(), is("Dry run for course 'Aqua Power 50 Min.' failed! DurationToWait=3600, Evaluated courseIdTac=14389398, evaluatedBearerToken:null"));
      assertThat(migrosApiBookCourseResponse.courseBookResult(), is(CourseBookResult.COURSE_BOOKING_DRY_RUN_FAILED));
   }

   @Test
   void cancelCourse() {
      // Given
      String username = "username";
      Supplier<char[]> userPwd = "pasd"::toCharArray;
      int PORT = 8282;
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + BOOK_COURSE_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod(HttpMethod.DELETE.name())
              .withRequestBody(CANCEL_COURSE_TAC_ID_REQUEST)
              .withResponseBody(CANCEL_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      AuthenticationContainer authenticationContainer = new AuthenticationContainer(username, userPwd);
      MigrosApiCancelCourseRequest migrosApiCancelCourseRequest = new MigrosApiCancelCourseRequest(BOOKING_ID_TAC);
      MigrosApiCancelCourseResponse migrosApiBookCourseResponse = migrosApi.cancelCourse(authenticationContainer, migrosApiCancelCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.courseCancelResult(), is(CourseCancelResult.COURSE_CANCELED));
   }

   @Test
   void cancelCourseDueToException() {
      // Given
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + PORT + BOOK_COURSE_PATH, HOST + ":" + PORT + BOOK_COURSE_PATH, BEARER_TOKEN_PROVIDER, HTTP_SERVICE);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(PORT)
              .withHost(HOST)
              .withRequestResponse(BOOK_COURSE_PATH)
              .withMethod(HttpMethod.DELETE.name())
              .withRequestBody(CANCEL_COURSE_TAC_ID_REQUEST)
              .withResponseBody("{")
              .withHeader(new Header(AuthConst.AUTHORIZATION, BEARER_TOKEN))
              .buildRequestResponse()
              .build();

      // When
      AuthenticationContainer authenticationContainer = new AuthenticationContainer("dontcare", "pasd"::toCharArray);
      MigrosApiCancelCourseRequest migrosApiCancelCourseRequest = new MigrosApiCancelCourseRequest(BOOKING_ID_TAC);
      MigrosApiCancelCourseResponse migrosApiBookCourseResponse = migrosApi.cancelCourse(authenticationContainer, migrosApiCancelCourseRequest);

      // Then
      serverTestCaseBuilder.stop();
      assertThat(migrosApiBookCourseResponse.courseCancelResult(), is(CourseCancelResult.COURSE_CANCEL_FAILED));
   }

   private static Optional<MigrosCourse> findCourse4Name(List<MigrosCourse> courses, String courseName) {
      return courses.stream()
              .filter(course -> course.courseName().equals(courseName))
              .findFirst();
   }
}