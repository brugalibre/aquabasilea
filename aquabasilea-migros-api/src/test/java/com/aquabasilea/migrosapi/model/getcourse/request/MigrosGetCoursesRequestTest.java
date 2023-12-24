package com.aquabasilea.migrosapi.model.getcourse.request;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MigrosGetCoursesRequestTest {

   @Test
   void isEmptyRequest() {
      // Given
      MigrosGetCoursesRequest migrosGetCoursesRequest = new MigrosGetCoursesRequest(List.of(), List.of(), List.of(), "8", false);

      // When
      boolean actualEmptyRequest = migrosGetCoursesRequest.isEmptyRequest();

      // Then
      assertThat(actualEmptyRequest, is(true));
   }

   @Test
   void isEmptyRequest_NotEmpty() {
      // Given
      MigrosGetCoursesRequest migrosGetCoursesRequest = new MigrosGetCoursesRequest(List.of(), List.of("abc"), List.of(), "8", false);

      // When
      boolean actualEmptyRequest = migrosGetCoursesRequest.isEmptyRequest();

      // Then
      assertThat(actualEmptyRequest, is(false));
   }

   @Test
   void isEmptyRequest_NotEmpty2() {
      // Given
      MigrosGetCoursesRequest migrosGetCoursesRequest = new MigrosGetCoursesRequest(List.of(), List.of(), List.of("drs"), "8", false);

      // When
      boolean actualEmptyRequest = migrosGetCoursesRequest.isEmptyRequest();

      // Then
      assertThat(actualEmptyRequest, is(false));
   }

   @Test
   void isEmptyRequest_NotEmpty3() {
      // Given
      MigrosGetCoursesRequest migrosGetCoursesRequest = new MigrosGetCoursesRequest(List.of("3"), List.of(), List.of(), "8", false);

      // When
      boolean actualEmptyRequest = migrosGetCoursesRequest.isEmptyRequest();

      // Then
      assertThat(actualEmptyRequest, is(false));
   }
}