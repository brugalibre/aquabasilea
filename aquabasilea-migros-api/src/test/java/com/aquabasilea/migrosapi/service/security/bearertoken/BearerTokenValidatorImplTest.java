package com.aquabasilea.migrosapi.service.security.bearertoken;

import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenValidator;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.common.http.service.HttpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class BearerTokenValidatorImplTest {

   @Test
   void isBearerTokenUnauthorizedHappyCase() {
      // Given
      HttpService httpService = mock(HttpService.class);
      BookCourseHelper bookCourseHelper = new BookCourseHelper("", "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidatorImpl(bookCourseHelper, httpService);
      Mockito.when(httpService.callRequestAndParse(any(), any())).thenReturn(ResponseWrapper.of(new Object(), 200));

      // When
      boolean actualBearerTokenUnauthorized = bearerTokenValidator.isBearerTokenUnauthorized("1234");

      // Then
      assertThat(actualBearerTokenUnauthorized, is(false));
   }

   @Test
   void isBearerTokenUnauthorizedUnauthorized() {
      // Given
      HttpService httpService = mock(HttpService.class);
      BookCourseHelper bookCourseHelper = new BookCourseHelper("", "");
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidatorImpl(bookCourseHelper, httpService);
      Mockito.when(httpService.callRequestAndParse(any(), any())).thenReturn(ResponseWrapper.of(new Object(), 401));

      // When
      boolean actualBearerTokenUnauthorized = bearerTokenValidator.isBearerTokenUnauthorized("1234");

      // Then
      assertThat(actualBearerTokenUnauthorized, is(true));
   }

   @Test
   void isBearerTokenUnauthorizedNullToken() {
      // Given
      BearerTokenValidator bearerTokenValidator = new BearerTokenValidatorImpl(mock(HttpService.class));

      // When
      boolean actualBearerTokenUnauthorized = bearerTokenValidator.isBearerTokenUnauthorized(null);

      // Then
      assertThat(actualBearerTokenUnauthorized, is(true));
   }
}