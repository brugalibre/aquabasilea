package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.mapper.MigrosCourseMapper;
import com.aquabasilea.migrosapi.mapper.MigrosCourseMapperImpl;
import com.aquabasilea.migrosapi.model.book.api.CourseBookResult;
import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.book.api.MigrosBookContext;
import com.aquabasilea.migrosapi.model.getcourse.request.MigrosRequestCourse;
import com.aquabasilea.migrosapi.model.getcourse.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.MigrosResponseCourse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosCourse;
import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;
import com.aquabasilea.migrosapi.service.book.MigrosBookCourseResponseReader;
import com.aquabasilea.migrosapi.service.getcourse.MigrosGetCoursesResponseReader;
import com.aquabasilea.migrosapi.service.security.MigrosApiBearerTokenProviderImpl;
import com.aquabasilea.migrosapi.service.security.api.BearerTokenProvider;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.model.getcourse.request.api.MigrosApiGetCoursesRequest.DEFAULT_TAKE;
import static com.aquabasilea.migrosapi.service.MigrosApiConst.*;
import static java.util.Objects.nonNull;

public class MigrosApiImpl implements MigrosApi {

   private static final Logger LOG = LoggerFactory.getLogger(MigrosApiImpl.class);
   private final MigrosCourseMapper migrosCourseMapper;
   private final BearerTokenProvider bearerTokenProvider;

   private final HttpService httpService;
   private final String migrosCourseBookUrl;
   private final String migrosGetCoursesUrl;
   private final String migrosGetCoursesRequestBody;
   private final String migrosBookCourseRequestBody;

   /**
    * Creates a default {@link MigrosApiImpl} with the {@link MigrosApiBearerTokenProviderImpl}
    * as well as default config values
    */
   public MigrosApiImpl() {
      this(new MigrosApiBearerTokenProviderImpl());
   }

   /**
    * Creates a default {@link MigrosApiImpl} with the given {@link BearerTokenProvider}
    * as well as default config values
    */
   public MigrosApiImpl(BearerTokenProvider bearerTokenProvider) {
      this(MIGROS_BOOKING_URL, MIGROS_GET_COURSES_URL, bearerTokenProvider);
   }

   MigrosApiImpl(String migrosCourseBookUrl, String migrosGetCoursesUrl, BearerTokenProvider bearerTokenProvider) {
      this.migrosCourseBookUrl = migrosCourseBookUrl;
      this.migrosGetCoursesUrl = migrosGetCoursesUrl;
      this.bearerTokenProvider = bearerTokenProvider;
      this.httpService = new HttpService();
      this.migrosCourseMapper = new MigrosCourseMapperImpl();
      this.migrosGetCoursesRequestBody = MIGROS_GET_COURSES_REQUEST_BODY;
      this.migrosBookCourseRequestBody = MIGROS_BOOK_COURSE_REQUEST_BODY;
   }

   public static void main(String[] args) {
      MigrosApiImpl migrosApi = new MigrosApiImpl();
      List<MigrosCourse> migrosResponseCours = migrosApi.getCourses(MigrosApiGetCoursesRequest.of(List.of(args))).courses();
      printFoundCourses(migrosResponseCours);
//      migrosApi.userAuthenticated(new AuthenticationContext(TOKEN));
//      migrosApi.bookCourse(new MigrosApiBookCourseRequest("CORExpress 25 Min.", "1", "139"));
   }

   private static void printFoundCourses(List<MigrosCourse> migrosCourses) {
      System.out.println("Found '" + migrosCourses.size() + "' courses:");
      System.out.println("============================");
      for (MigrosCourse migrosCours : migrosCourses) {
         System.out.println(migrosCours + "\n");
      }
      System.out.println("============================");
   }

   @Override
   public MigrosApiGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      List<MigrosResponseCourse> migrosResponseCourses = getMigrosCourses(migrosApiGetCoursesRequest);
      return new MigrosApiGetCoursesResponse(migrosCourseMapper.mapToMigrosCourses(migrosResponseCourses));
   }

   private List<MigrosResponseCourse> getMigrosCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      LOG.info("Evaluating courses for request {}", migrosApiGetCoursesRequest);
      HttpRequest httpGetCourseRequest = getMigrosGetAllCourseHttpRequest(migrosApiGetCoursesRequest);
      MigrosGetCoursesResponse migrosGetCoursesResponse = httpService.callRequestAndParse(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      LOG.info("Evaluated {} courses ", migrosGetCoursesResponse.getResultCount());
      return migrosGetCoursesResponse.getCourses();
   }

   private HttpRequest getMigrosGetAllCourseHttpRequest(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      return HttpRequest.getHttpPostRequest(migrosGetCoursesRequestBody
                      .replace(TAKE_PLACEHOLDER, migrosApiGetCoursesRequest.take())
                      .replace(CENTER_IDS_PLACEHOLDER, joinStrings2String(migrosApiGetCoursesRequest.courseCenterIds()))
                      .replace(COURSE_TITLES_PLACEHOLDER, joinStrings2String(migrosApiGetCoursesRequest.courseTitles()))
                      .replace(WEEK_DAY_PLACEHOLDER, joinStrings2String(migrosApiGetCoursesRequest.dayIds()))
              , migrosGetCoursesUrl);
   }

   @Override
   public MigrosApiBookCourseResponse bookCourse(AuthenticationContainer authenticationContainer,
                                                 MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      try {
         return bookCourseInternal(authenticationContainer, migrosApiBookCourseRequest);
      } catch (Exception e) {
         LOG.error("Error while booking course {}", migrosApiBookCourseRequest.courseName(), e);
         return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED, e.getMessage());
      }
   }

   private MigrosApiBookCourseResponse bookCourseInternal(AuthenticationContainer authenticationContainer, MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      LOG.info("Try to book course '{}'", migrosApiBookCourseRequest);
      MigrosBookContext migrosBookContext = migrosApiBookCourseRequest.migrosBookContext();
      String bearerToken = getAndSetBearerAuthentication(authenticationContainer);
      String courseIdTac = getCourseIdTac(migrosApiBookCourseRequest);
      if (migrosBookContext.dryRun()) {
         return handleDryRun(migrosApiBookCourseRequest, courseIdTac, bearerToken);
      }
      LOG.info("Got a non-null bearer token={} and courseIdTac={}", nonNull(bearerToken), courseIdTac);
      waitUntilCourseIsBookable(migrosBookContext.duration2WaitUntilCourseBecomesBookable());
      MigrosBookCourseResponse migrosBookCourseResponse = createAndPostBookCourseRequest(migrosApiBookCourseRequest.centerId(), courseIdTac);
      LOG.info("Booking result for course '{}' is '{}'", migrosApiBookCourseRequest.courseName(), migrosBookCourseResponse.getCourseBookResult());
      return new MigrosApiBookCourseResponse(migrosBookCourseResponse.getCourseBookResult(), migrosBookCourseResponse.getMessage());
   }

   private void waitUntilCourseIsBookable(Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      try {
         LOG.info("Going to wait for {}s plus {}ms offset until the course will be bookable..", duration2WaitUntilCourseBecomesBookable.get().getSeconds(), BOOK_COURSE_OFFSET_MS);
         Thread.sleep(duration2WaitUntilCourseBecomesBookable.get().toMillis() + BOOK_COURSE_OFFSET_MS);
      } catch (InterruptedException e) {
         LOG.warn("Interrupted while waiting for the course to become bookable! Time left: {}", duration2WaitUntilCourseBecomesBookable.get().toMillis());
      }
   }

   private MigrosApiBookCourseResponse handleDryRun(MigrosApiBookCourseRequest migrosApiBookCourseRequest, String courseIdTac, String bearerToken) {
      if (nonNull(courseIdTac) && nonNull(bearerToken)) {
         LOG.info("Dry run for course '{}' successful", migrosApiBookCourseRequest.courseName());
         return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_BOOKING_DRY_RUN_SUCCESSFUL, "");
      }
      String errorMsg = "Dry run for course '%s' failed! Evaluated courseIdTac=%s, evaluatedBearerToken:%s".formatted(migrosApiBookCourseRequest.courseName(), courseIdTac, bearerToken);
      LOG.error(errorMsg, courseIdTac, bearerToken);
      return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_BOOKING_DRY_RUN_FAILED, errorMsg);
   }

   private String getAndSetBearerAuthentication(AuthenticationContainer authenticationContainer) {
      String bearerToken = bearerTokenProvider.getBearerToken(authenticationContainer.username(), authenticationContainer.userPwdSupplier());
      httpService.setCredentials(bearerToken);
      return bearerToken;
   }

   private String getCourseIdTac(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      HttpRequest httpGetCourseRequest = getMigrosGetSingleCourseHttpRequest(migrosApiBookCourseRequest.centerId(), migrosApiBookCourseRequest.courseName(), migrosApiBookCourseRequest.weekDay());
      MigrosGetCoursesResponse migrosGetCoursesResponse = httpService.callRequestAndParse(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
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

   private MigrosBookCourseResponse createAndPostBookCourseRequest(String centerId, String courseIdTac) {
      HttpRequest httpBookRequest = getBookCourseHttpRequest(centerId, courseIdTac);
      LOG.info("Created Http-Request {}", httpBookRequest);
      return httpService.callRequestAndParse(new MigrosBookCourseResponseReader(), httpBookRequest);
   }

   private HttpRequest getBookCourseHttpRequest(String centerId, String courseIdTac) {
      return HttpRequest.getHttpPostRequest(migrosBookCourseRequestBody.replace(CENTER_ID_PLACEHOLDER, centerId)
              .replace(COURSE_ID_TAC_PLACEHOLDER, courseIdTac), migrosCourseBookUrl);
   }

   private static String joinStrings2String(List<String> elements) {
      return String.join(",", elements);
   }
}
