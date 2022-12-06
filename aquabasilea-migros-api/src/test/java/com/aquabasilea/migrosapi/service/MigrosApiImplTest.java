package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.http.DummyHttpServerTestCaseBuilder;
import com.aquabasilea.migrosapi.model.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.response.api.MigrosApGetCoursesResponse;
import com.aquabasilea.migrosapi.model.response.api.MigrosCourse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;

import java.util.List;
import java.util.Optional;

import static com.aquabasilea.migrosapi.service.TestRequestResponse.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MigrosApiImplTest {

   private static final String HOST = "http://127.0.0.1";

   @Test
   void getCourses() {
      // Given
      int port = 8282;
      String path = "/kp/api/Courselist/all?";
      MigrosApi migrosApi = new MigrosApiImpl("", HOST + ":" + port + path);
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(port)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod("POST")
              .withResponseBody(RESPONSE_1)
              .withRequestBody(REQUEST_1)
              .withHeader(new Header("Authorization", ""))
              .buildRequestResponse()
              .build();

      // When
      MigrosApGetCoursesResponse coursesResponse = migrosApi.getCourses(MigrosApiGetCoursesRequest.of(List.of("129", "139")));

      // Then
      assertThat(coursesResponse.courses().size(), is(2));
      Optional<MigrosCourse> course1Opt = findCourse4Name(coursesResponse, COURSE_NAME_1);
      assertThat(course1Opt.isPresent(), is(true));
      assertThat(course1Opt.get().centerId(), is("129"));
      Optional<MigrosCourse> course2Opt = findCourse4Name(coursesResponse, COURSE_NAME_2);
      assertThat(course2Opt.isPresent(), is(true));
      assertThat(course2Opt.get().centerId(), is("139"));
   }

   private static Optional<MigrosCourse> findCourse4Name(MigrosApGetCoursesResponse coursesResponse, String courseName) {
      return coursesResponse.courses()
              .stream()
              .filter(course -> course.courseName().equals(courseName))
              .findFirst();
   }
}