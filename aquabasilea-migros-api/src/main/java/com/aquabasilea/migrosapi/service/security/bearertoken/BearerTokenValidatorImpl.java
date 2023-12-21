package com.aquabasilea.migrosapi.service.security.bearertoken;

import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.service.book.MigrosGetBookedCoursesResponseReader;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenValidator;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.MIGROS_BOOKING_URL;
import static com.aquabasilea.migrosapi.service.MigrosApiConst.MIGROS_BOOK_COURSE_REQUEST_BODY;

/**
 * This {@link BearerTokenValidatorImpl} uses the http-request and retrieves the booked courses as a validation
 * It's more like a hack actually, but works pretty well
 */
public class BearerTokenValidatorImpl implements BearerTokenValidator {

   private static final Logger LOG = LoggerFactory.getLogger(BearerTokenValidatorImpl.class);
   private final BookCourseHelper bookCourseHelper;
   private final HttpService httpService;

   public BearerTokenValidatorImpl(HttpService httpService) {
      this(new BookCourseHelper(MIGROS_BOOKING_URL, MIGROS_BOOK_COURSE_REQUEST_BODY), httpService);
   }

   public BearerTokenValidatorImpl(BookCourseHelper bookCourseHelper, HttpService httpService) {
      this.bookCourseHelper = bookCourseHelper;
      this.httpService = httpService;
   }

   @Override
   public boolean isBearerTokenUnauthorized(String bearerToken) {
      LOG.info("Validate token {}", bearerToken);
      if (bearerToken == null) {
         return true;
      }
      HttpRequest httpGetCourseRequest = bookCourseHelper.getBookedCoursesRequest(bearerToken);
      return httpService.callRequestAndParse(new MigrosGetBookedCoursesResponseReader(), httpGetCourseRequest).statusCode() == 401;
   }
}
