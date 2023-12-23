package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.repository.mapping.CoursesEntityMapper;
import com.aquabasilea.domain.course.repository.mapping.CoursesEntityMapperImpl;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR;
import static com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;

public class MigrosApiCourseBookerFacadeImpl implements AquabasileaCourseBookerFacade {

   private final MigrosApi migrosApi;
   private final CoursesEntityMapper coursesEntityMapper;

   private final Supplier<Duration> duration2WaitUntilCourseBecomesBookable;
   private final Supplier<char[]> userPasswordSupplier;
   private final String userName;

   public MigrosApiCourseBookerFacadeImpl(MigrosApi migrosApi, String userName,
                                          Supplier<char[]> userPasswordSupplier,
                                          Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      this.migrosApi = migrosApi;
      this.userName = userName;
      this.coursesEntityMapper = new CoursesEntityMapperImpl();
      this.userPasswordSupplier = userPasswordSupplier;
      this.duration2WaitUntilCourseBecomesBookable = duration2WaitUntilCourseBecomesBookable;
   }

   @Override
   public List<Course> getBookedCourses() {
      MigrosApiGetBookedCoursesResponse migrosApiGetBookedCoursesResponse = migrosApi.getBookedCourses(getAuthenticationContainer());
      return coursesEntityMapper.mapMigrosCourses2Courses(migrosApiGetBookedCoursesResponse.courses());
   }

   @Override
   public CourseCancelResult cancelCourses(String bookingId) {
      MigrosApiCancelCourseRequest migrosApiCancelCourseRequest = new MigrosApiCancelCourseRequest(bookingId);
      MigrosApiCancelCourseResponse migrosApiCancelCourseResponse = migrosApi.cancelCourse(getAuthenticationContainer(), migrosApiCancelCourseRequest);
      return map2CourseCanelResult(migrosApiCancelCourseResponse.courseCancelResult());
   }

   private static CourseCancelResult map2CourseCanelResult(com.aquabasilea.migrosapi.v1.model.book.response.CourseCancelResult courseCancelResult) {
      return switch (courseCancelResult) {
         case COURSE_CANCELED -> CourseCancelResult.COURSE_CANCELED;
         case COURSE_CANCEL_FAILED -> CourseCancelResult.COURSE_CANCEL_FAILED;
      };
   }

   @Override
   public CourseBookingResultDetails selectAndBookCourse(CourseBookContainer courseBookContainer) {
      CourseBookDetails courseBookDetails = courseBookContainer.courseBookDetails();
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = getMigrosApiBookCourseRequest(duration2WaitUntilCourseBecomesBookable, courseBookContainer);
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(getAuthenticationContainer(), migrosApiBookCourseRequest);
      return mapApiResponse2CourseBookingEndResult(courseBookDetails.courseName(), migrosApiBookCourseResponse);
   }

   private static CourseBookingResultDetails mapApiResponse2CourseBookingEndResult(String courseName, MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      String errorMessage = getErrorMessage(migrosApiBookCourseResponse);
      return CourseBookingResultDetailsImpl.of(mapMigrosCourseBookResult(migrosApiBookCourseResponse), courseName, errorMessage);
   }

   private static String getErrorMessage(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      if (isErrorOccurred(migrosApiBookCourseResponse)
              && migrosApiBookCourseResponse.errorMsg() == null) {
         return "unknown!";
      }
      return migrosApiBookCourseResponse.errorMsg();
   }

   private static boolean isErrorOccurred(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return migrosApiBookCourseResponse.courseBookResult() == COURSE_NOT_BOOKABLE_TECHNICAL_ERROR ||
              migrosApiBookCourseResponse.courseBookResult() == COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
   }

   private static CourseBookResult mapMigrosCourseBookResult(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return switch (migrosApiBookCourseResponse.courseBookResult()) {
         case COURSE_BOOKED -> CourseBookResult.BOOKED;
         case COURSE_NOT_BOOKABLE_FULLY_BOOKED -> CourseBookResult.NOT_BOOKED_COURSE_FULLY_BOOKED;
         case COURSE_NOT_BOOKABLE_ALREADY_BOOKED -> CourseBookResult.NOT_BOOKED_COURSE_ALREADY_BOOKED;
         case COURSE_NOT_BOOKABLE_TECHNICAL_ERROR -> CourseBookResult.NOT_BOOKED_TECHNICAL_ERROR;
         case COURSE_NOT_BOOKED_UNEXPECTED_ERROR -> CourseBookResult.NOT_BOOKED_UNEXPECTED_ERROR;
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED -> CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED;
         case COURSE_BOOKING_DRY_RUN_SUCCESSFUL -> CourseBookResult.DRY_RUN_SUCCESSFUL;
         case COURSE_BOOKING_DRY_RUN_FAILED -> CourseBookResult.DRY_RUN_FAILED;
      };
   }

   private MigrosApiBookCourseRequest getMigrosApiBookCourseRequest(Supplier<Duration> duration2WaitUntilCourseBecomesBookable, CourseBookContainer courseBookContainer) {
      return mapCourseBookDetails2ApiRequest(courseBookContainer.courseBookDetails(),
              courseBookContainer.bookingContext().dryRun(), duration2WaitUntilCourseBecomesBookable);
   }

   private static MigrosApiBookCourseRequest mapCourseBookDetails2ApiRequest(CourseBookDetails courseBookDetails, boolean dryRun,
                                                                             Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      String weekDayValue = String.valueOf(courseBookDetails.courseDate().getDayOfWeek().getValue());
      MigrosBookContext migrosBookContext = new MigrosBookContext(dryRun, duration2WaitUntilCourseBecomesBookable);
      return new MigrosApiBookCourseRequest(courseBookDetails.courseName(), weekDayValue, courseBookDetails.courseLocation()
              .getId(), migrosBookContext);
   }

   private AuthenticationContainer getAuthenticationContainer() {
      return new AuthenticationContainer(userName, userPasswordSupplier);
   }
}
