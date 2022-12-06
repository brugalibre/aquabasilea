package com.aquabasilea.migrosapi.service;

import com.aquabasilea.migrosapi.http.model.request.HttpRequest;
import com.aquabasilea.migrosapi.http.service.HttpService;
import com.aquabasilea.migrosapi.mapper.MigrosCourseMapper;
import com.aquabasilea.migrosapi.mapper.MigrosCourseMapperImpl;
import com.aquabasilea.migrosapi.model.request.MigrosRequestCourse;
import com.aquabasilea.migrosapi.model.request.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.response.MigrosBookCourseResponse;
import com.aquabasilea.migrosapi.model.response.MigrosGetCoursesResponse;
import com.aquabasilea.migrosapi.model.response.MigrosResponseCourse;
import com.aquabasilea.migrosapi.model.response.api.MigrosApGetCoursesResponse;
import com.aquabasilea.migrosapi.model.response.api.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.model.response.api.MigrosCourse;
import com.aquabasilea.migrosapi.service.response.MigrosBookCourseResponseReader;
import com.aquabasilea.migrosapi.service.response.MigrosGetCoursesResponseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.*;

public class MigrosApiImpl implements MigrosApi {

   private static final Logger LOG = LoggerFactory.getLogger(MigrosApiImpl.class);
   private final MigrosCourseMapper migrosCourseMapper;

   private final HttpService httpService;
   private final String migrosCourseBookUrl;
   private final String migrosGetCoursesUrl;
   private final String migrosGetCoursesRequestBody;
   private final String migrosBookCourseRequestBody;

   public MigrosApiImpl() {
      this(MIGROS_BOOKING_URL, MIGROS_GET_COURSES_URL);
   }

   MigrosApiImpl(String migrosCourseBookUrl, String migrosGetCoursesUrl) {
      this.migrosCourseBookUrl = migrosCourseBookUrl;
      this.migrosGetCoursesUrl = migrosGetCoursesUrl;
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
   public MigrosApGetCoursesResponse getCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      LOG.info("Evaluating courses for request {}", migrosApiGetCoursesRequest);
      List<MigrosResponseCourse> migrosResponseCourses = getMigrosCourses(migrosApiGetCoursesRequest);
      return new MigrosApGetCoursesResponse(migrosCourseMapper.mapToMigrosCourses(migrosResponseCourses));
   }

   private List<MigrosResponseCourse> getMigrosCourses(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
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
   public MigrosApiBookCourseResponse bookCourse(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      LOG.info("Try to book course '{}'", migrosApiBookCourseRequest);
      String courseIdTac = getCourseIdTac(migrosApiBookCourseRequest);
      MigrosBookCourseResponse migrosBookCourseResponse = createAndPostBookCourseRequest(migrosApiBookCourseRequest.centerId(), courseIdTac);
      LOG.info("Booking was successfully: {}", (migrosBookCourseResponse.isCourseSuccessfullyBooked() ? "yes" : "no"));
      return new MigrosApiBookCourseResponse();
   }

   private String getCourseIdTac(MigrosApiBookCourseRequest migrosApiBookCourseRequest) {
      HttpRequest httpGetCourseRequest = getMigrosGetSingleCourseHttpRequest(migrosApiBookCourseRequest.centerId(), migrosApiBookCourseRequest.courseName(), migrosApiBookCourseRequest.weekDay());
      MigrosGetCoursesResponse migrosGetCoursesResponse = httpService.callRequestAndParse(new MigrosGetCoursesResponseReader(), httpGetCourseRequest);
      return migrosGetCoursesResponse.getSingleCourseIdTac();
   }

   private HttpRequest getMigrosGetSingleCourseHttpRequest(String centerId, String courseName, String weekDay) {
      return HttpRequest.getHttpPostRequest(migrosGetCoursesRequestBody
                      .replace(CENTER_IDS_PLACEHOLDER, centerId)
                      .replace(COURSE_TITLES_PLACEHOLDER, new MigrosRequestCourse(centerId, courseName).toJson())
                      .replace(WEEK_DAY_PLACEHOLDER, weekDay)
              , migrosGetCoursesUrl);
   }

   private MigrosBookCourseResponse createAndPostBookCourseRequest(String centerId, String courseIdTac) {
      HttpRequest httpBookRequest = getBookCourseHttpRequest(centerId, courseIdTac);
      return httpService.callRequestAndParse(new MigrosBookCourseResponseReader(), httpBookRequest);
   }

   private HttpRequest getBookCourseHttpRequest(String centerId, String courseIdTac) {
      return HttpRequest.getHttpPostRequest(migrosBookCourseRequestBody.replace(CENTER_ID_PLACEHOLDER, centerId)
              .replace(COURSE_ID_TAC_PLACEHOLDER, courseIdTac), migrosCourseBookUrl);
   }

   private static String joinStrings2String(List<String> elements) {
      return elements.stream()
              .collect(Collectors.joining(","));
   }
}
