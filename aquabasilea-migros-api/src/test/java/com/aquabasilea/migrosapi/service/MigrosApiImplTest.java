package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.book.api.MigrosBookContext;
import com.aquabasilea.migrosapi.model.getcourse.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosCourse;
import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;
import com.aquabasilea.migrosapi.service.security.api.BearerTokenProvider;
import com.brugalibre.common.http.auth.AuthConst;
import com.brugalibre.test.http.DummyHttpServerTestCaseBuilder;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.service.TestRequestResponse.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MigrosApiImplTest {

   private static final String HOST = "http://127.0.0.1";
   private static final BearerTokenProvider BEARER_TOKEN_PROVIDER = (username, pwd) -> "bearer-token";

   @Test
   void getCourses() {
      // Given
      int port = 8282;
      String path = "/kp/api/Courselist/all?";
      MigrosApi migrosApi = new MigrosApiImpl("", HOST + ":" + port + path, BEARER_TOKEN_PROVIDER);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(port)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod("POST")
              .withResponseBody(GET_COURSES_RESPONSE)
              .withRequestBody(GET_COURSES_REQUEST)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .build();

      // When
      MigrosApiGetCoursesResponse coursesResponse = migrosApi.getCourses(MigrosApiGetCoursesRequest.of(List.of("129", "139")));

      // Then
      assertThat(coursesResponse.courses().size(), is(2));
      Optional<MigrosCourse> course1Opt = findCourse4Name(coursesResponse, COURSE_NAME_1);
      assertThat(course1Opt.isPresent(), is(true));
      assertThat(course1Opt.get().centerId(), is("129"));
      Optional<MigrosCourse> course2Opt = findCourse4Name(coursesResponse, COURSE_NAME_2);
      assertThat(course2Opt.isPresent(), is(true));
      assertThat(course2Opt.get().centerId(), is("139"));
   }

//   @Test
   void bookCourse() {
      // Given
      int port = 8283;
      String getCoursesPath = "/kp/api/Courselist/all?";
      String bookCoursePath = "/kp/api/Booking?";
      MigrosApi migrosApi = new MigrosApiImpl(HOST + ":" + port + bookCoursePath, HOST + ":" + port + getCoursesPath, BEARER_TOKEN_PROVIDER);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(port)
              .withHost(HOST)
              .withRequestResponse(getCoursesPath)
              .withMethod("GET")
              .withRequestBody(GET_COURSE_TAC_ID_REQUEST)
              .withResponseBody(GET_COURSE_TAC_ID_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, ""))
              .buildRequestResponse()
              .withRequestResponse(bookCoursePath)
              .withMethod("POST")
              .withRequestBody(BOOK_COURSE_REQUEST)
              .withResponseBody(BOOK_COURSE_RESPONSE)
              .withHeader(new Header(AuthConst.AUTHORIZATION, "Bearer 1234"))
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
      assertThat(migrosApiBookCourseResponse.errorMsg(), is(nullValue()));
   }

   private static Optional<MigrosCourse> findCourse4Name(MigrosApiGetCoursesResponse coursesResponse, String courseName) {
      return coursesResponse.courses()
              .stream()
              .filter(course -> course.courseName().equals(courseName))
              .findFirst();
   }
}