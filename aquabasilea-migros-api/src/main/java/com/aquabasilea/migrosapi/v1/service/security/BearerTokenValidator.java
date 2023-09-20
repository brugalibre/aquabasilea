package com.aquabasilea.migrosapi.v1.service.security;

import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.service.book.MigrosGetBookedCoursesResponseReader;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.service.HttpService;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.MIGROS_BOOKING_URL;
import static com.aquabasilea.migrosapi.service.MigrosApiConst.MIGROS_BOOK_COURSE_REQUEST_BODY;

/**
 * The {@link BearerTokenValidator} validates a bearer token
 */
public class BearerTokenValidator {

   private final BookCourseHelper bookCourseHelper;
   private final HttpService httpService;

   public BearerTokenValidator() {
      this(new BookCourseHelper(MIGROS_BOOKING_URL, MIGROS_BOOK_COURSE_REQUEST_BODY), new HttpService(30));
   }

   public BearerTokenValidator(BookCourseHelper bookCourseHelper, HttpService httpService) {
      this.bookCourseHelper = bookCourseHelper;
      this.httpService = httpService;
   }

   /**
    * Validates the given bearer token.
    * It is valid if and only if the given token is still authorized and the call to the migros api returns 401!
    *
    * @param bearerToken the bearer token the
    * @return <code>true</code> if the given token is still valid or <code>false</code> if not
    */
   public boolean isBearerTokenUnauthorized(String bearerToken) {
      if (bearerToken == null) {
         return false;
      }
      httpService.setCredentials(bearerToken);
      HttpRequest httpGetCourseRequest = bookCourseHelper.getBookedCoursesRequest();
      return httpService.callRequestAndParse(new MigrosGetBookedCoursesResponseReader(), httpGetCourseRequest).statusCode() == 401;
   }
}
