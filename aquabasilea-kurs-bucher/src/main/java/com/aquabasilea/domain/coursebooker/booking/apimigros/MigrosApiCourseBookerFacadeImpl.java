package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.course.repository.mapping.CoursesEntityMapper;
import com.aquabasilea.domain.course.repository.mapping.CoursesEntityMapperImpl;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.v1.model.book.response.MigrosApiCancelCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiGetBookedCoursesResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

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
      return switch (courseCancelResult){
         case COURSE_CANCELED -> CourseCancelResult.COURSE_CANCELED;
         case COURSE_CANCEL_FAILED -> CourseCancelResult.COURSE_CANCEL_FAILED;
      };
   }

   @Override
   public CourseBookingEndResult selectAndBookCourse(CourseBookContainer courseBookContainer) {
      CourseBookDetails courseBookDetails = courseBookContainer.courseBookDetails();
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = getMigrosApiBookCourseRequest(duration2WaitUntilCourseBecomesBookable, courseBookContainer);
      MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(getAuthenticationContainer(), migrosApiBookCourseRequest);
      return mapApiResponse2CourseBookingEndResult(courseBookDetails.courseName(), migrosApiBookCourseResponse);
   }

   private static CourseBookingEndResult mapApiResponse2CourseBookingEndResult(String courseName, MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return CourseBookingEndResult.CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(mapMigrosCourseBookResult(migrosApiBookCourseResponse))
              .withCourseName(courseName)
              .withErrors(map2Errors(migrosApiBookCourseResponse))
              .build();
   }

   private static List<String> map2Errors(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return StringUtils.isNotEmpty(migrosApiBookCourseResponse.errorMsg()) ? List.of(migrosApiBookCourseResponse.errorMsg()) : List.of();
   }

   private static CourseClickedResult mapMigrosCourseBookResult(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return switch (migrosApiBookCourseResponse.courseBookResult()) {
         case COURSE_NOT_BOOKED_UNEXPECTED_ERROR,
                 COURSE_NOT_BOOKABLE_ALREADY_BOOKED,
                 COURSE_NOT_BOOKABLE_TECHNICAL_ERROR,
                 COURSE_BOOKING_DRY_RUN_FAILED -> CourseClickedResult.COURSE_NOT_BOOKABLE;
         case COURSE_NOT_BOOKABLE_FULLY_BOOKED -> CourseClickedResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED;
         case COURSE_BOOKED -> CourseClickedResult.COURSE_BOOKED;
         case COURSE_BOOKING_DRY_RUN_SUCCESSFUL -> CourseClickedResult.COURSE_BOOKING_ABORTED;
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED -> CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
      };
   }

   private MigrosApiBookCourseRequest getMigrosApiBookCourseRequest(Supplier<Duration> duration2WaitUntilCourseBecomesBookable, CourseBookContainer courseBookContainer) {
      return mapCourseBookDetails2ApiRequest(courseBookContainer.courseBookDetails(),
              courseBookContainer.bookingContext().dryRun(), duration2WaitUntilCourseBecomesBookable);
   }

   private static MigrosApiBookCourseRequest mapCourseBookDetails2ApiRequest(CourseBookDetails courseBookDetails, boolean dryRun,
                                                                             Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      CourseLocation courseLocation = CourseLocation.fromDisplayName(courseBookDetails.courseLocation());
      String weekDayValue = String.valueOf(courseBookDetails.courseDate().getDayOfWeek().getValue());
      MigrosBookContext migrosBookContext = new MigrosBookContext(dryRun, duration2WaitUntilCourseBecomesBookable);
      return new MigrosApiBookCourseRequest(courseBookDetails.courseName(), weekDayValue, courseLocation.getId(), migrosBookContext);
   }

   private AuthenticationContainer getAuthenticationContainer() {
      return new AuthenticationContainer(userName, userPasswordSupplier);
   }
}
