package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RetryBookMigrosApiTest {

   @Test
   void bookCourseWithRetries() {
      // Given
      MigrosApi migrosApi = mockMigrosApi(CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR, "error");
      RetryBookMigrosApi retryBookMigrosApi = new RetryBookMigrosApi(migrosApi, 500, 3);
      AuthenticationContainer aut = new AuthenticationContainer("peter", "123"::toCharArray);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = MigrosApiBookCourseRequest.of("test", "monday", "4", () -> Duration.ofMinutes(3));
      MigrosApiBookCourseRequest copiedApiBookCourseRequest = MigrosApiBookCourseRequest.of(migrosApiBookCourseRequest.courseName(),
              migrosApiBookCourseRequest.weekDay(), migrosApiBookCourseRequest.centerId(), retryBookMigrosApi.getZeroDelayDurationSupplier());

      // When
      MigrosApiBookCourseResponse actualResponse = retryBookMigrosApi.bookCourse(aut, migrosApiBookCourseRequest);

      // Then
      verify(migrosApi).bookCourse(eq(aut), eq(migrosApiBookCourseRequest));
      verify(migrosApi, times(3)).bookCourse(eq(aut), eq(copiedApiBookCourseRequest));
      MatcherAssert.assertThat(actualResponse.courseBookResult(), is(CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR));
   }

   @Test
   void bookCourseWithNoRetries() {
      // Given
      MigrosApi migrosApi = mockMigrosApi(CourseBookResult.COURSE_BOOKED, null);
      AuthenticationContainer aut = new AuthenticationContainer("peter", "123"::toCharArray);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = MigrosApiBookCourseRequest.of("test", "monday", "4", () -> Duration.ofMinutes(3));
      RetryBookMigrosApi retryBookMigrosApi = new RetryBookMigrosApi(migrosApi, 500, 3);

      // When
      MigrosApiBookCourseResponse actualResponse = retryBookMigrosApi.bookCourse(aut, migrosApiBookCourseRequest);

      // Then
      verify(migrosApi).bookCourse(eq(aut), eq(migrosApiBookCourseRequest));
      MatcherAssert.assertThat(actualResponse.courseBookResult(), is(CourseBookResult.COURSE_BOOKED));
   }

   private static MigrosApi mockMigrosApi(CourseBookResult courseBookResult, String errorMsg) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      when(migrosApi.bookCourse(any(), any())).thenReturn(new MigrosApiBookCourseResponse(courseBookResult, errorMsg));
      return migrosApi;
   }
}