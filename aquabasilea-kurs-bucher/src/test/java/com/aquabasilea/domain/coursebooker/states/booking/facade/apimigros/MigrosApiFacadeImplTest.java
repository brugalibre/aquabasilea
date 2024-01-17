package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros;

import com.aquabasilea.application.security.service.AuthenticationContainerService;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResultDetails;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping.MigrosCourseMapper;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.api.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosCourse;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class MigrosApiFacadeImplTest {

   @Test
   void bookCourse_NoErrors() {

      // Given
      String username = "username";
      Supplier<char[]> userPwdCharSup = "dsf"::toCharArray;
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(32);

      CourseLocation migrosFitnesscenterAquabasilea = MIGROS_FITNESSCENTER_AQUABASILEA;
      String coursename = "coursename";
      LocalDateTime courseDate = LocalDateTime.now();
      String weekday = String.valueOf(courseDate.getDayOfWeek().getValue());
      CourseBookDetails courseBookDetails = new CourseBookDetails(coursename, "leyla", courseDate, migrosFitnesscenterAquabasilea);
      CourseBookContainer courseBookContainer1 = new CourseBookContainer(courseBookDetails, new BookingContext(true));
      CourseBookContainer courseBookContainer2 = new CourseBookContainer(courseBookDetails, new BookingContext(false));

      com.aquabasilea.application.security.model.AuthenticationContainer actualAuthContainer = new com.aquabasilea.application.security.model.AuthenticationContainer(username, userPwdCharSup);
      com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer expectedAuthContainer = new com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer(username, userPwdCharSup);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest1 = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.centerId()), new MigrosBookContext(true, durationSupplier));
      MigrosApiBookCourseRequest migrosApiBookCourseRequest2 = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.centerId()), new MigrosBookContext(false, durationSupplier));

      MigrosApi migrosApi = mockMigrosApiBookCourse(expectedAuthContainer, "", com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult.COURSE_BOOKED);
      AuthenticationContainerService authenticationContainerService = mockAuthenticationContainerService(actualAuthContainer);
      AuthenticationContainerRegistry authenticationContainerRegistry = new AuthenticationContainerRegistry(authenticationContainerService);
      MigrosApiProvider migrosApiProvider = new MigrosApiProvider(migrosApi, null, authenticationContainerRegistry, mockCourseLocationRepository(), null);
      CourseBookerFacadeFactory courseBookerFacadeFactory = new CourseBookerFacadeFactory(migrosApiProvider);

      // When
      CourseBookerFacade courseBookerFacade1 = courseBookerFacadeFactory.createCourseBookerFacade(username, durationSupplier);
      CourseBookerFacade courseBookerFacade2 = courseBookerFacadeFactory.createCourseBookerFacade(username, durationSupplier);

      CourseBookingResultDetails CourseBookingResultDetails1 = courseBookerFacade1.bookCourse(courseBookContainer1);
      CourseBookingResultDetails CourseBookingResultDetails2 = courseBookerFacade2.bookCourse(courseBookContainer2);

      // Then
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest1));
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest2));

      assertThat(CourseBookingResultDetails1.getCourseBookResult(), is(CourseBookResult.BOOKED));
      assertThat(CourseBookingResultDetails2.getCourseBookResult(), is(CourseBookResult.BOOKED));
   }

   @Test
   void bookCourse_WithErrors() {

      // Given
      String username = "username";
      Supplier<char[]> userPwdCharSup = "dsf"::toCharArray;
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(32);

      CourseLocation migrosFitnesscenterAquabasilea = MIGROS_FITNESSCENTER_AQUABASILEA;
      String coursename = "coursename";
      LocalDateTime courseDate = LocalDateTime.now();
      String weekday = String.valueOf(courseDate.getDayOfWeek().getValue());
      CourseBookDetails courseBookDetails = new CourseBookDetails(coursename, "leyla", courseDate, migrosFitnesscenterAquabasilea);
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(true));

      com.aquabasilea.application.security.model.AuthenticationContainer actualAuthContainer = new com.aquabasilea.application.security.model.AuthenticationContainer(username, userPwdCharSup);
      com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer expectedAuthContainer = new com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer(username, userPwdCharSup);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.centerId()), new MigrosBookContext(true, durationSupplier));

      String error = "error";
      MigrosApi migrosApi = mockMigrosApiBookCourse(expectedAuthContainer, error, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED);
      AuthenticationContainerService authenticationContainerService = mockAuthenticationContainerService(actualAuthContainer);
      AuthenticationContainerRegistry authenticationContainerRegistry = new AuthenticationContainerRegistry(authenticationContainerService);
      MigrosApiProvider migrosApiProvider = new MigrosApiProvider(migrosApi, null, authenticationContainerRegistry, mockCourseLocationRepository(), null);
      CourseBookerFacadeFactory courseBookerFacadeFactory = new CourseBookerFacadeFactory(migrosApiProvider);

      // When
      CourseBookerFacade courseBookerFacade = courseBookerFacadeFactory.createCourseBookerFacade(username, durationSupplier);
      CourseBookingResultDetails CourseBookingResultDetails = courseBookerFacade.bookCourse(courseBookContainer);

      // Then
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest));
      assertThat(CourseBookingResultDetails.getCourseBookResult(), is(CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED));
      assertThat(CourseBookingResultDetails.getErrorMessage(), is(error));
   }

   @Test
   void bookCourse_WithErrorButNoErrorMessageProvided() {

      // Given
      String username = "username";
      Supplier<char[]> userPwdCharSup = "dsf"::toCharArray;
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(32);

      CourseLocation migrosFitnesscenterAquabasilea = MIGROS_FITNESSCENTER_AQUABASILEA;
      String coursename = "coursename";
      LocalDateTime courseDate = LocalDateTime.now();
      String weekday = String.valueOf(courseDate.getDayOfWeek().getValue());
      CourseBookDetails courseBookDetails = new CourseBookDetails(coursename, "leyla", courseDate, migrosFitnesscenterAquabasilea);
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(true));

      com.aquabasilea.application.security.model.AuthenticationContainer actualAuthContainer = new com.aquabasilea.application.security.model.AuthenticationContainer(username, userPwdCharSup);
      AuthenticationContainer expectedAuthContainer = new AuthenticationContainer(username, userPwdCharSup);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.centerId()), new MigrosBookContext(true, durationSupplier));

      String error = null;
      MigrosApi migrosApi = mockMigrosApiBookCourse(expectedAuthContainer, error, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED);
      AuthenticationContainerService authenticationContainerService = mockAuthenticationContainerService(actualAuthContainer);
      AuthenticationContainerRegistry authenticationContainerRegistry = new AuthenticationContainerRegistry(authenticationContainerService);
      MigrosApiProvider migrosApiProvider = new MigrosApiProvider(migrosApi, null, authenticationContainerRegistry, mockCourseLocationRepository(), null);
      CourseBookerFacadeFactory courseBookerFacadeFactory = new CourseBookerFacadeFactory(migrosApiProvider);

      // When
      CourseBookerFacade courseBookerFacade = courseBookerFacadeFactory.createCourseBookerFacade(username, durationSupplier);
      CourseBookingResultDetails CourseBookingResultDetails = courseBookerFacade.bookCourse(courseBookContainer);

      // Then
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest));
      assertThat(CourseBookingResultDetails.getCourseBookResult(), is(CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED));
      assertThat(CourseBookingResultDetails.getErrorMessage(), is("unknown!"));
   }

   @Test
   void getCourses() {
      // Given
      String courseInstructor = "Clara";
      String courseName = "COURSE_2";
      String userId1 = "userId1";
      MigrosApi migrosApi = mockMigrosApiGetCourses(courseInstructor, courseName, MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      MigrosCourseMapper migrosCourseMapper = MigrosCourseMapper.of(mockCourseLocationRepository());
      MigrosApiCourseDefExtractor migrosApiCourseDefExtractor = new MigrosApiCourseDefExtractor(migrosApi, getAuthenticationContainerRegistry(), migrosCourseMapper);
      CourseBookerFacade courseBookerFacade = new MigrosApiFacadeImpl(migrosApi, migrosApiCourseDefExtractor, migrosCourseMapper, () -> new AuthenticationContainer("", ""::toCharArray),
              () -> Duration.ZERO);

      // When
      List<CourseDef> courseDefs = courseBookerFacade.getCourseDefs(userId1, List.of());

      // Then
      assertThat(courseDefs.size(), is(1));
      CourseDef courseDef = courseDefs.get(0);
      assertThat(courseDef.courseName(), is(courseName));
      assertThat(courseDef.courseLocation(), is(MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(courseDef.courseInstructor(), is(courseInstructor));
      assertThat(courseDef.userId(), is(userId1));
   }

   @Test
   void cancelCourse_CancelSuccessful() {
      // Given
      String bookingId = "12354";
      MigrosApi migrosApi = mockMigrosApiCancelCourse(bookingId, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseCancelResult.COURSE_CANCELED);
      MigrosCourseMapper migrosCourseMapper = MigrosCourseMapper.of(mockCourseLocationRepository());
      MigrosApiCourseDefExtractor migrosApiCourseDefExtractor = new MigrosApiCourseDefExtractor(migrosApi, getAuthenticationContainerRegistry(), migrosCourseMapper);
      CourseBookerFacade courseBookerFacade = new MigrosApiFacadeImpl(migrosApi, migrosApiCourseDefExtractor, migrosCourseMapper, () -> new AuthenticationContainer("", ""::toCharArray),
              () -> Duration.ZERO);

      // When
      CourseCancelResultDetails courseCancelResultDetails = courseBookerFacade.cancelCourses(bookingId);

      // Then
      assertThat(courseCancelResultDetails.courseCancelResult(), is(CourseCancelResult.COURSE_CANCELED));
   }

   @Test
   void cancelCourse_CancelFailed() {
      // Given
      String bookingId = "12354";
      MigrosApi migrosApi = mockMigrosApiCancelCourse(bookingId, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseCancelResult.COURSE_CANCEL_FAILED);
      MigrosCourseMapper migrosCourseMapper = MigrosCourseMapper.of(mockCourseLocationRepository());
      MigrosApiCourseDefExtractor migrosApiCourseDefExtractor = new MigrosApiCourseDefExtractor(migrosApi, getAuthenticationContainerRegistry(), migrosCourseMapper);
      CourseBookerFacade courseBookerFacade = new MigrosApiFacadeImpl(migrosApi, migrosApiCourseDefExtractor, migrosCourseMapper, () -> new AuthenticationContainer("", ""::toCharArray),
              () -> Duration.ZERO);

      // When
      CourseCancelResultDetails courseCancelResultDetails = courseBookerFacade.cancelCourses(bookingId);

      // Then
      assertThat(courseCancelResultDetails.courseCancelResult(), is(CourseCancelResult.COURSE_CANCEL_FAILED));
   }

   @Test
   void getBookedCourses() {
      // Given
      //LocalDateTime courseDate, String centerId, String courseName, String courseInstructor, String bookingIdTac
      LocalDateTime courseDate = LocalDateTime.now();
      String centerId = MIGROS_FITNESSCENTER_AQUABASILEA.centerId();
      String courseInstructor = "Petra Sturzenegger";
      String courseName = "course1";
      String bookingIdTac = "12354";
      List<MigrosCourse> migrosCourses = List.of(new MigrosCourse(courseDate, centerId, courseName, courseInstructor, bookingIdTac));
      MigrosApi migrosApi = mockMigrosApiGetBookedCourses(migrosCourses);
      MigrosCourseMapper migrosCourseMapper = MigrosCourseMapper.of(mockCourseLocationRepository());
      MigrosApiCourseDefExtractor migrosApiCourseDefExtractor = new MigrosApiCourseDefExtractor(migrosApi, getAuthenticationContainerRegistry(), migrosCourseMapper);
      CourseBookerFacade courseBookerFacade = new MigrosApiFacadeImpl(migrosApi, migrosApiCourseDefExtractor, migrosCourseMapper, () -> new AuthenticationContainer("", ""::toCharArray),
              () -> Duration.ZERO);

      // When
      List<Course> bookedCourses = courseBookerFacade.getBookedCourses();

      // Then
      assertThat(bookedCourses.size(), is(1));
      Course course = bookedCourses.get(0);
      assertThat(course.getCourseInstructor(), is(courseInstructor));
      assertThat(course.getCourseName(), is(courseName));
      assertThat(course.getBookingIdTac(), is(bookingIdTac));
      assertThat(course.getCourseDate(), is(courseDate));
      assertThat(course.getCourseLocation(), is(MIGROS_FITNESSCENTER_AQUABASILEA));
   }

   private static CourseLocationRepository mockCourseLocationRepository() {
      CourseLocationRepository courseLocationRepository = mock(CourseLocationRepository.class);
      when(courseLocationRepository.findByCenterId(eq(MIGROS_FITNESSCENTER_AQUABASILEA.centerId()))).thenReturn(MIGROS_FITNESSCENTER_AQUABASILEA);
      return courseLocationRepository;
   }

   private MigrosApi mockMigrosApiGetBookedCourses(List<MigrosCourse> migrosCourses) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      mockGetBookedCourses(migrosApi, migrosCourses);
      return migrosApi;
   }

   private void mockGetBookedCourses(MigrosApi migrosApi, List<MigrosCourse> migrosCourses) {
      MigrosApiGetBookedCoursesResponse migrosApiGetBookedCoursesResponse = new MigrosApiGetBookedCoursesResponse(migrosCourses);
      when(migrosApi.getBookedCourses(any())).thenReturn(migrosApiGetBookedCoursesResponse);
   }

   private MigrosApi mockMigrosApiCancelCourse(String bookingId, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseCancelResult cancelResult) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      mockCancelCourse(migrosApi, bookingId, cancelResult);
      return migrosApi;
   }

   private void mockCancelCourse(MigrosApi migrosApi, String bookingId, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseCancelResult cancelResult) {
      MigrosApiCancelCourseRequest migrosApiCancelCourseRequest = new MigrosApiCancelCourseRequest(bookingId);
      when(migrosApi.cancelCourse(any(), eq(migrosApiCancelCourseRequest))).thenReturn(new MigrosApiCancelCourseResponse(cancelResult));
   }

   private static MigrosApi mockMigrosApiBookCourse(com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer expectedAuthContainer,
                                                    String errorMsg, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult courseBookResult) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      mockBookCourse(migrosApi, expectedAuthContainer, errorMsg, courseBookResult);
      return migrosApi;
   }

   private static MigrosApi mockMigrosApiGetCourses(String courseInstructor, String courseName, String centerId) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      mockGetCourses(migrosApi, courseInstructor, courseName, centerId);
      return migrosApi;
   }

   private static void mockGetCourses(MigrosApi migrosApi, String courseInstructor, String courseName, String centerId) {
      List<MigrosCourse> courses = List.of(new MigrosCourse(LocalDateTime.now(), centerId, courseName, courseInstructor, null));
      MigrosApiGetCoursesResponse migrosApiGetCoursesResponse = new MigrosApiGetCoursesResponse(courses);
      when(migrosApi.getCourses(any(), any())).thenReturn(migrosApiGetCoursesResponse);
   }

   private static void mockBookCourse(MigrosApi migrosApi, com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer expectedAuthContainer,
                                      String errorMsg, com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult courseBookResult) {
      when(migrosApi.bookCourse(eq(expectedAuthContainer), any())).thenReturn(new MigrosApiBookCourseResponse(courseBookResult, errorMsg));
   }

   private static AuthenticationContainerRegistry getAuthenticationContainerRegistry() {
      AuthenticationContainerService authenticationContainerService = mockAuthenticationContainerService(new com.aquabasilea.application.security.model.AuthenticationContainer("peter", "heinz"::toCharArray));
      return new AuthenticationContainerRegistry(authenticationContainerService);
   }

   private static AuthenticationContainerService mockAuthenticationContainerService(com.aquabasilea.application.security.model.AuthenticationContainer actualAuthContainer) {
      AuthenticationContainerService authenticationContainerService = mock (AuthenticationContainerService.class);
      when(authenticationContainerService.getAuthenticationContainer(any())).thenReturn(actualAuthContainer);
      return authenticationContainerService;
   }

}