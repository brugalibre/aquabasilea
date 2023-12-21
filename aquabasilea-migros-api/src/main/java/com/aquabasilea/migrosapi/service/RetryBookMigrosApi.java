package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.service.util.WaitUtil;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.api.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

public class RetryBookMigrosApi implements MigrosApi {
   private final static Logger LOG = LoggerFactory.getLogger(RetryBookMigrosApi.class);
   private final MigrosApi migrosApi;
   private final int retryTimeout;
   private final int retries;

   RetryBookMigrosApi(MigrosApi migrosApi, int retryTimeout, int retries) {
      this.migrosApi = migrosApi;
      this.retryTimeout = retryTimeout;
      this.retries = retries;
   }

   public RetryBookMigrosApi(MigrosApi migrosApi) {
      this(migrosApi, 1500, 3);
   }

   @Override
   public MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer, MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      return bookCourseWithRetry(authenticationContainer, migrosApiBookCourseRequest, retries);
   }

   private MigrosApiBookCourseResponse bookCourseWithRetry(AuthenticationContainer authenticationContainer, MigrosApiBookCourseRequest migrosApiBookCourseRequest, int retries) {
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);
      if (migrosApiBookCourseResponse.courseBookResult() == CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR && retries > 0) {
         LOG.warn("Booking failed due to error {}", migrosApiBookCourseResponse.courseBookResult());
         WaitUtil.suspendCurrentThread(retryTimeout);
         MigrosApiBookCourseRequest copyMigrosApiBookCourseRequest = creatyCopyMigrosApiBookCourseRequestWithZeroDelay(migrosApiBookCourseRequest);
         return bookCourseWithRetry(authenticationContainer, copyMigrosApiBookCourseRequest, --retries);
      }
      return migrosApiBookCourseResponse;
   }

   @Override
   public MigrosApiCancelCourseResponse cancelCourse(AuthenticationContainer authenticationContainer, MigrosApiCancelCourseRequest migrosApiCancelCourseRequest) {
      return migrosApi.cancelCourse(authenticationContainer, migrosApiCancelCourseRequest);
   }

   @Override
   public MigrosApiGetBookedCoursesResponse getBookedCourses(AuthenticationContainer authenticationContainer) {
      return migrosApi.getBookedCourses(authenticationContainer);
   }

   @Override
   public MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      return migrosApi.getCourses(migrosApiGetCoursesRequest);
   }

   private MigrosApiBookCourseRequest creatyCopyMigrosApiBookCourseRequestWithZeroDelay(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      return MigrosApiBookCourseRequest.of(migrosApiBookCourseRequest.courseName()
              , migrosApiBookCourseRequest.weekDay(),
              migrosApiBookCourseRequest.centerId(), getZeroDelayDurationSupplier());

   }

   Supplier<Duration> getZeroDelayDurationSupplier() {
      return () -> Duration.ZERO;
   }
}
