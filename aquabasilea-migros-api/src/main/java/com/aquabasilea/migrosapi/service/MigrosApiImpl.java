package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.mapper.MigrosCourseMapper;
import com.aquabasilea.migrosapi.mapper.MigrosCourseMapperImpl;
import com.aquabasilea.migrosapi.model.book.response.MigrosCancelCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.request.MigrosGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.request.MigrosRequestCourse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosResponseCourse;
import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.service.book.MigrosBookCourseResponseReader;
import com.aquabasilea.migrosapi.service.book.MigrosCancelCourseResponseReader;
import com.aquabasilea.migrosapi.service.book.MigrosGetBookedCoursesResponseReader;
import com.aquabasilea.migrosapi.service.getcourse.MigrosGetCoursesResponseReader;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.common.http.service.HttpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.*;
import static com.aquabasilea.migrosapi.v1.model.getcourse.request.MigrosApiGetCoursesRequest.DEFAULT_TAKE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MigrosApiImpl implements MigrosApi {

   private static final Logger LOG = LoggerFactory.getLogger(MigrosApiImpl.class);
   private final MigrosCourseMapper migrosCourseMapper;
   private final BearerTokenProvider bearerTokenProvider;

   private final HttpService httpService;
   private final BookCourseHelper bookCourseHelper;

   private final String migrosGetCoursesUrl;
   private final String migrosGetCoursesRequestBody;

   /**
    * Creates a default {@link MigrosApiImpl} with the given {@link BearerTokenProvider}
    * as well as default config values
    */
   public MigrosApiImpl(BearerTokenProvider bearerTokenProvider) {
      this(MIGROS_BOOKING_URL, MIGROS_GET_COURSES_URL, bearerTokenProvider);
   }
   MigrosApiImpl(String migrosCourseBookUrl, String migrosGetCoursesUrl, BearerTokenProvider bearerTokenProvider) {
      this.migrosGetCoursesUrl = migrosGetCoursesUrl;
      this.bearerTokenProvider = bearerTokenProvider;
      this.httpService = new HttpService(30);
      this.migrosCourseMapper = new MigrosCourseMapperImpl();
      this.migrosGetCoursesRequestBody = MIGROS_GET_COURSES_REQUEST_BODY;
      this.bookCourseHelper = new BookCourseHelper(migrosCourseBookUrl, MIGROS_BOOK_COURSE_REQUEST_BODY);
   }

   @Override
   public MigrosApiGetBookedCoursesResponse getBookedCourses(AuthenticationContainer authenticationContainer) {
      LOG.info("Fetching booked courses for user [{}]", authenticationContainer.username());
      getAndSetBearerAuthentication(authenticationContainer);
      HttpRequest httpGetCourseRequest = bookCourseHelper.getBookedCoursesRequest();
      ResponseWrapper<List<MigrosResponseCourse>> responseWrapper = httpService.callRequestAndParse(new MigrosGetBookedCoursesResponseReader(), httpGetCourseRequest);
      logResponse(responseWrapper, httpGetCourseRequest);
      return new MigrosApiGetBookedCoursesResponse(migrosCourseMapper.mapToMigrosCourses(responseWrapper.httpResponse()));
   }

   @Override
   public MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      List<MigrosResponseCourse> migrosResponseCourses = getMigrosCourses(MigrosGetCoursesRequest.of(migrosApiGetCoursesRequest));
      return new MigrosApiGetCoursesResponse(migrosCourseMapper.mapToMigrosCourses(migrosResponseCourses));
   }

   private List<MigrosResponseCourse> getMigrosCourses(MigrosGetCoursesRequest migrosGetCoursesRequest) {
      LOG.info("Evaluating courses for request {}", migrosGetCoursesRequest);
      HttpRequest httpGetCourseRequest = getMigrosGetAllCourseHttpRequest(migrosGetCoursesRequest);
      ResponseWrapper<MigrosGetCoursesResponse> responseWrapper = httpService.callRequestAndParse(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      MigrosGetCoursesResponse migrosGetCoursesResponse = responseWrapper.httpResponse();
      LOG.info("Got response {}, evaluated {} courses ", responseWrapper, migrosGetCoursesResponse.getResultCount());
      return migrosGetCoursesResponse.getCourses();
   }

   private HttpRequest getMigrosGetAllCourseHttpRequest(MigrosGetCoursesRequest migrosGetCoursesRequest) {
      return HttpRequest.getHttpPostRequest(migrosGetCoursesRequestBody
                      .replace(TAKE_PLACEHOLDER, migrosGetCoursesRequest.take())
                      .replace(CENTER_IDS_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.courseCenterIds()))
                      .replace(COURSE_TITLES_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.courseTitles()))
                      .replace(WEEK_DAY_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.dayIds()))
              , migrosGetCoursesUrl);
   }

   @Override
   public MigrosApiCancelCourseResponse cancelCourse(AuthenticationContainer authenticationContainer, MigrosApiCancelCourseRequest migrosApiCancelCourseRequest) {
      LOG.info("Cancel booked course '{}' for user [{}]", migrosApiCancelCourseRequest.courseBookingId(), authenticationContainer.username());
      getAndSetBearerAuthentication(authenticationContainer);
      HttpRequest cancelCourseRequest = bookCourseHelper.getCancelCourseRequest(migrosApiCancelCourseRequest);
      ResponseWrapper<MigrosCancelCourseResponse> responseWrapper = httpService.callRequestAndParse(new MigrosCancelCourseResponseReader(), cancelCourseRequest);
      logResponse(responseWrapper, cancelCourseRequest);
      return MigrosApiCancelCourseResponse.of(responseWrapper.httpResponse(), migrosApiCancelCourseRequest.courseBookingId());
   }

   @Override
   public MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer,
                                                 MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      LOG.info("Try to book course '{}' for user [{}]", migrosApiBookCourseRequest, authenticationContainer.username());
      MigrosBookContext migrosBookContext = migrosApiBookCourseRequest.migrosBookContext();
      String bearerToken = getAndSetBearerAuthentication(authenticationContainer);
      String courseIdTac = getCourseIdTac(migrosApiBookCourseRequest);
      if (migrosBookContext.dryRun()) {
         return handleDryRun(migrosApiBookCourseRequest, courseIdTac, bearerToken);
      }
      LOG.info("Got a non-null bearer token={} and courseIdTac={} for user [{}]", StringUtils.isNotEmpty(bearerToken), courseIdTac, authenticationContainer.username());
      waitUntilCourseIsBookable(migrosBookContext.duration2WaitUntilCourseBecomesBookable());
      HttpRequest httpBookRequest = bookCourseHelper.getBookCourseHttpRequest(migrosApiBookCourseRequest.centerId(), courseIdTac);
      ResponseWrapper<MigrosBookCourseResponse> migrosBookCourseResponseWrapper = httpService.callRequestAndParse(new MigrosBookCourseResponseReader(), httpBookRequest);
      logResponse(migrosBookCourseResponseWrapper, httpBookRequest);
      return bookCourseHelper.unwrapAndCreateApiBookCourseResponse(migrosBookCourseResponseWrapper);
   }

   private void waitUntilCourseIsBookable(Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      try {
         LOG.info("Going to wait for {}s plus {}ms offset until the course will be bookable..", duration2WaitUntilCourseBecomesBookable.get().getSeconds(), BOOK_COURSE_OFFSET_MS);
         long effectiveSleep = Math.max(0, duration2WaitUntilCourseBecomesBookable.get().toMillis() + BOOK_COURSE_OFFSET_MS);
         Thread.sleep(effectiveSleep);
      } catch (InterruptedException e) {
         LOG.warn("Interrupted while waiting for the course to become bookable! Time left: {}", duration2WaitUntilCourseBecomesBookable.get().toMillis());
      }
   }

   private MigrosApiBookCourseResponse handleDryRun(MigrosApiBookCourseRequest migrosApiBookCourseRequest, String courseIdTac, String bearerToken) {
      if (isValidRequestValues(courseIdTac, bearerToken)) {
         LOG.info("Dry run for course '{}' successful", migrosApiBookCourseRequest.courseName());
         return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_BOOKING_DRY_RUN_SUCCESSFUL, "");
      }
      String bearerTokenLogMsg = StringUtils.isNotEmpty(bearerToken) ? "yes" : "null";
      String errorMsg = "Dry run for course '%s' failed! DurationToWait=%s, Evaluated courseIdTac=%s, evaluatedBearerToken:%s"
              .formatted(migrosApiBookCourseRequest.courseName(), getDuration(migrosApiBookCourseRequest), courseIdTac, bearerTokenLogMsg);
      LOG.error(errorMsg, courseIdTac, bearerToken);
      return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_BOOKING_DRY_RUN_FAILED, errorMsg);
   }

   private static boolean isValidRequestValues(String courseIdTac, String bearerToken) {
      return nonNull(courseIdTac)
              && StringUtils.isNotEmpty(bearerToken);
   }

   private static long getDuration(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      Supplier<Duration> durationSupplier = migrosApiBookCourseRequest.migrosBookContext().duration2WaitUntilCourseBecomesBookable();
      return durationSupplier.get()
              .get(ChronoUnit.SECONDS);
   }

   private String getAndSetBearerAuthentication(AuthenticationContainer authenticationContainer) {
      String bearerToken = bearerTokenProvider.getBearerToken(authenticationContainer.username(), authenticationContainer.userPwdSupplier());
      if (isNull(bearerToken)) {
         LOG.warn("Bearer token is null, change to empty String");
         bearerToken = "";// avoid setting a null value as credentials since this leads to an NPE
      }
      LOG.info("Authentication successful for user [{}] {}?", authenticationContainer.username(), StringUtils.isNotEmpty(bearerToken) ? "Yes" : "No");
      httpService.setCredentials(bearerToken);
      return bearerToken;
   }

   private String getCourseIdTac(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      HttpRequest httpGetCourseRequest = getMigrosGetSingleCourseHttpRequest(migrosApiBookCourseRequest.centerId(), migrosApiBookCourseRequest.courseName(), migrosApiBookCourseRequest.weekDay());
      MigrosGetCoursesResponse migrosGetCoursesResponse = httpService.callRequestParseAndUnwrap(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      return migrosGetCoursesResponse.getSingleCourseIdTac();
   }

   private HttpRequest getMigrosGetSingleCourseHttpRequest(String centerId, String courseName, String weekDay) {
      return HttpRequest.getHttpPostRequest(migrosGetCoursesRequestBody
                      .replace(TAKE_PLACEHOLDER, DEFAULT_TAKE)
                      .replace(CENTER_IDS_PLACEHOLDER, centerId)
                      .replace(COURSE_TITLES_PLACEHOLDER, new MigrosRequestCourse(centerId, courseName).toJson())
                      .replace(WEEK_DAY_PLACEHOLDER, weekDay)
              , migrosGetCoursesUrl);
   }

   private static String joinStrings2String(List<String> elements) {
      return String.join(",", elements);
   }

   private static void logResponse(ResponseWrapper<?> responseWrapper, HttpRequest httpRequest) {
      if (nonNull(responseWrapper.exception())) {
         LOG.error("Request {} failed!", httpRequest, responseWrapper.exception());
      }else if (!responseWrapper.successful()) {
         LOG.warn("Request {} failed!", httpRequest);
      }
   }
}
