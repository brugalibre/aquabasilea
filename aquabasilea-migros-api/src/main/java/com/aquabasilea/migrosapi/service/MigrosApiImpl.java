package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.api.v1.model.getcenters.request.MigrosApiGetCentersRequest;
import com.aquabasilea.migrosapi.api.v1.model.getcenters.response.MigrosApiGetCentersResponse;
import com.aquabasilea.migrosapi.mapper.MigrosMapper;
import com.aquabasilea.migrosapi.mapper.MigrosMapperImpl;
import com.aquabasilea.migrosapi.model.book.response.MigrosCancelCourseResponse;
import com.aquabasilea.migrosapi.model.getcenters.request.MigrosGetCentersRequest;
import com.aquabasilea.migrosapi.model.getcenters.response.MigrosGetCentersResponse;
import com.aquabasilea.migrosapi.model.getcenters.response.MigrosResponseCenter;
import com.aquabasilea.migrosapi.model.getcourse.request.MigrosGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.request.MigrosRequestCourse;
import com.aquabasilea.migrosapi.model.book.response.MigrosBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosResponseCourse;
import com.aquabasilea.migrosapi.service.book.BookCourseHelper;
import com.aquabasilea.migrosapi.service.book.MigrosBookCourseResponseReader;
import com.aquabasilea.migrosapi.service.book.MigrosCancelCourseResponseReader;
import com.aquabasilea.migrosapi.service.book.MigrosGetBookedCoursesResponseReader;
import com.aquabasilea.migrosapi.service.config.UrlConfig;
import com.aquabasilea.migrosapi.service.getcenters.MigrosGetCentersResponseReader;
import com.aquabasilea.migrosapi.service.getcourse.MigrosGetCoursesResponseReader;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.api.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.migrosapi.service.util.StringUtils;
import com.brugalibre.common.http.model.method.HttpMethod;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.common.http.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.service.config.MigrosApiConst.*;
import static com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest.DEFAULT_TAKE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MigrosApiImpl implements MigrosApi {

   private static final Logger LOG = LoggerFactory.getLogger(MigrosApiImpl.class);
   private final MigrosMapper migrosMapper;
   private final BearerTokenProvider bearerTokenProvider;

   private final HttpService httpService;
   private final BookCourseHelper bookCourseHelper;

   private final UrlConfig urlConfig;
   private final String migrosGetCoursesRequestBody;

   /**
    * Creates a default {@link MigrosApiImpl} with the given {@link BearerTokenProvider}
    * as well as default config values
    *
    * @param bearerTokenProvider the {@link BearerTokenProvider} responsible for authentication
    * @param httpService         the {@link HttpService} which does the actual http-communication
    */
   public MigrosApiImpl(BearerTokenProvider bearerTokenProvider, HttpService httpService) {
      this(new UrlConfig(), bearerTokenProvider,
              httpService);
   }

   MigrosApiImpl(UrlConfig urlConfig, BearerTokenProvider bearerTokenProvider, HttpService httpService) {
      this.urlConfig = urlConfig;
      this.bearerTokenProvider = bearerTokenProvider;
      this.httpService = httpService;
      this.migrosMapper = new MigrosMapperImpl();
      this.migrosGetCoursesRequestBody = MIGROS_GET_COURSES_REQUEST_BODY;
      this.bookCourseHelper = new BookCourseHelper(urlConfig, MIGROS_BOOK_COURSE_REQUEST_BODY);
   }

   @Override
   public MigrosApiGetBookedCoursesResponse getBookedCourses(AuthenticationContainer authenticationContainer) {
      LOG.info("Fetching booked courses");
      String bearerAuthentication = getBearerAuthentication(authenticationContainer);
      HttpRequest httpGetCourseRequest = bookCourseHelper.getBookedCoursesRequest(bearerAuthentication);
      ResponseWrapper<List<MigrosResponseCourse>> responseWrapper = httpService.callRequestAndParse(new MigrosGetBookedCoursesResponseReader(), httpGetCourseRequest);
      logResponse(responseWrapper, httpGetCourseRequest);
      return new MigrosApiGetBookedCoursesResponse(migrosMapper.mapToMigrosCourses(responseWrapper.httpResponse()));
   }

   @Override
   public MigrosApiGetCentersResponse getCenters(MigrosApiGetCentersRequest migrosApiGetCentersRequest) {
      List<MigrosResponseCenter> migrosResponseCourses = getMigrosCenters(MigrosGetCentersRequest.of(migrosApiGetCentersRequest));
      return new MigrosApiGetCentersResponse(migrosMapper.mapToMigrosCenters(migrosResponseCourses));
   }

   private List<MigrosResponseCenter> getMigrosCenters(MigrosGetCentersRequest migrosGetCentersRequest) {
      LOG.info("Evaluating all centers for request={}", migrosGetCentersRequest);
      HttpRequest httpGetCentersRequest = HttpRequest.getHttpRequest(HttpMethod.GET, urlConfig.getMigrosGetCentersUrl());
      ResponseWrapper<List<MigrosGetCentersResponse>> responseWrapper = httpService.callRequestAndParse(new MigrosGetCentersResponseReader(), httpGetCentersRequest);
      List<MigrosResponseCenter> centers = unwrap(responseWrapper);
      LOG.info("Got response {}, evaluated {} centers ", responseWrapper, centers.size());
      return centers;
   }

   private static List<MigrosResponseCenter> unwrap(ResponseWrapper<List<MigrosGetCentersResponse>> responseWrapper) {
      List<MigrosGetCentersResponse> migrosGetCentersResponse = responseWrapper.httpResponse();
      return migrosGetCentersResponse.stream()
              .map(MigrosGetCentersResponse::getCenters)
              .flatMap(List::stream)
              .distinct()
              .toList();
   }

   @Override
   public MigrosApiGetCoursesResponse getCourses(AuthenticationContainer authenticationContainer,
                                                 MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      List<MigrosResponseCourse> migrosResponseCourses = getMigrosCourses(authenticationContainer, MigrosGetCoursesRequest.of(migrosApiGetCoursesRequest));
      return new MigrosApiGetCoursesResponse(migrosMapper.mapToMigrosCourses(migrosResponseCourses));
   }

   private List<MigrosResponseCourse> getMigrosCourses(AuthenticationContainer authenticationContainer,
                                                       MigrosGetCoursesRequest migrosGetCoursesRequest) {
      LOG.info("Evaluating courses for request {}", migrosGetCoursesRequest);
      if (migrosGetCoursesRequest.isEmptyRequest()) {
         LOG.warn("No center-ids provided! Need at least one center id in order to fetch courses");
         return List.of();
      }
      String bearerAuthentication = getBearerAuthentication(authenticationContainer);
      HttpRequest httpGetCourseRequest = getMigrosGetAllCourseHttpRequest(migrosGetCoursesRequest, bearerAuthentication);
      ResponseWrapper<MigrosGetCoursesResponse> responseWrapper = httpService.callRequestAndParse(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      MigrosGetCoursesResponse migrosGetCoursesResponse = responseWrapper.httpResponse();
      LOG.info("Got response {}, evaluated {} courses ", responseWrapper, migrosGetCoursesResponse.getResultCount());
      return migrosGetCoursesResponse.getCourses();
   }

   private HttpRequest getMigrosGetAllCourseHttpRequest(MigrosGetCoursesRequest migrosGetCoursesRequest, String bearerAuthentication) {
      return HttpRequest.getHttpRequest(HttpMethod.POST, urlConfig.getMigrosGetCoursesUrl())
              .withJsonBody(migrosGetCoursesRequestBody
                      .replace(TAKE_PLACEHOLDER, migrosGetCoursesRequest.take())
                      .replace(CENTER_IDS_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.courseCenterIds()))
                      .replace(COURSE_TITLES_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.courseTitles()))
                      .replace(WEEK_DAY_PLACEHOLDER, joinStrings2String(migrosGetCoursesRequest.dayIds())))
              .withAuthorization(bearerAuthentication);
   }

   @Override
   public MigrosApiCancelCourseResponse cancelCourse(AuthenticationContainer authenticationContainer, MigrosApiCancelCourseRequest migrosApiCancelCourseRequest) {
      LOG.info("Cancel booked course '{}'", migrosApiCancelCourseRequest.courseBookingId());
      String bearerToken = getBearerAuthentication(authenticationContainer);
      HttpRequest cancelCourseRequest = bookCourseHelper.getCancelCourseRequest(migrosApiCancelCourseRequest, bearerToken);
      ResponseWrapper<MigrosCancelCourseResponse> responseWrapper = httpService.callRequestAndParse(new MigrosCancelCourseResponseReader(), cancelCourseRequest);
      logResponse(responseWrapper, cancelCourseRequest);
      return MigrosApiCancelCourseResponse.of(responseWrapper.httpResponse(), migrosApiCancelCourseRequest.courseBookingId());
   }

   @Override
   public MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer,
                                                 MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      LOG.info("Try to book course '{}'", migrosApiBookCourseRequest);
      MigrosBookContext migrosBookContext = migrosApiBookCourseRequest.migrosBookContext();
      String bearerToken = getBearerAuthentication(authenticationContainer);
      String courseIdTac = getCourseIdTac(migrosApiBookCourseRequest, bearerToken);
      if (migrosBookContext.dryRun()) {
         return handleDryRun(migrosApiBookCourseRequest, courseIdTac, bearerToken);
      }
      LOG.info("Got a non-null bearer token={} and courseIdTac={}", StringUtils.isNotEmpty(bearerToken), courseIdTac);
      waitUntilCourseIsBookable(migrosBookContext.duration2WaitUntilCourseBecomesBookable());
      HttpRequest httpBookRequest = bookCourseHelper.getBookCourseHttpRequest(migrosApiBookCourseRequest.centerId(), courseIdTac, bearerToken);
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

   private String getBearerAuthentication(AuthenticationContainer authenticationContainer) {
      String bearerToken = bearerTokenProvider.getBearerToken(authenticationContainer.username(), authenticationContainer.userPwdSupplier());
      if (isNull(bearerToken)) {
         LOG.warn("Bearer token is null, change to empty String");
         bearerToken = "";// avoid setting a null value as credentials since this leads to an NPE
      }
      LOG.info("Authentication successful? {}", StringUtils.isNotEmpty(bearerToken) ? "Yes" : "No");
      return bearerToken;
   }

   private String getCourseIdTac(MigrosApiBookCourseRequest migrosApiBookCourseRequest, String bearerToken) {
      HttpRequest httpGetCourseRequest = getMigrosGetSingleCourseHttpRequest(migrosApiBookCourseRequest.centerId(), migrosApiBookCourseRequest.courseName(),
              migrosApiBookCourseRequest.weekDay(), bearerToken);
      MigrosGetCoursesResponse migrosGetCoursesResponse = httpService.callRequestParseAndUnwrap(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      return migrosGetCoursesResponse.getSingleCourseIdTac();
   }

   private HttpRequest getMigrosGetSingleCourseHttpRequest(String centerId, String courseName, String weekDay, String bearerToken) {
      return HttpRequest.getHttpRequest(HttpMethod.POST, urlConfig.getMigrosGetCoursesUrl())
              .withJsonBody(migrosGetCoursesRequestBody
                      .replace(TAKE_PLACEHOLDER, DEFAULT_TAKE)
                      .replace(CENTER_IDS_PLACEHOLDER, centerId)
                      .replace(COURSE_TITLES_PLACEHOLDER, new MigrosRequestCourse(centerId, courseName).toJson())
                      .replace(WEEK_DAY_PLACEHOLDER, weekDay))
              .withAuthorization(bearerToken);
   }

   private static String joinStrings2String(List<String> elements) {
      return String.join(",", elements);
   }

   private static void logResponse(ResponseWrapper<?> responseWrapper, HttpRequest httpRequest) {
      if (nonNull(responseWrapper.exception())) {
         LOG.error("Request {} failed!", httpRequest, responseWrapper.exception());
      } else if (!responseWrapper.successful()) {
         LOG.warn("Request {} failed!", httpRequest);
      }
   }
}
