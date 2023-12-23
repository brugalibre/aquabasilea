package com.aquabasilea.domain.coursebooker.booking.webmigros;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link AquabasileaCourseBookerFacade} implementation which relies on a selenium based implementation whic
 * clicks through the websites from migros
 */
public record MigrosWebCourseBookerFacadeImpl(String username, Supplier<char[]> userPassword,
                                              Supplier<Duration> duration2WaitUntilCourseBecomesBookable,
                                              String propertiesFile) implements AquabasileaCourseBookerFacade {
   @Override
   public CourseBookingResultDetails selectAndBookCourse(CourseBookContainer courseBookContainer) {
      AquabasileaWebCourseBooker aquabasileaWebNavigator = AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, userPassword.get(),
              courseBookContainer.bookingContext().dryRun(), duration2WaitUntilCourseBecomesBookable, propertiesFile);
      CourseBookDetails webNavigatorCourseBookDetails = mapToWebNavigatorCourseBookDetails(courseBookContainer.courseBookDetails());
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebNavigator.selectAndBookCourse(webNavigatorCourseBookDetails);
      return getCourseBookingResultDetails(courseBookingEndResult, courseBookContainer.bookingContext());
   }

   private CourseBookDetails mapToWebNavigatorCourseBookDetails(com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails courseBookDetails) {
      return new CourseBookDetails(courseBookDetails.courseName(), courseBookDetails.courseInstructor(),
              courseBookDetails.courseDate(),
              courseBookDetails.courseLocation().getCourseLocationName());
   }

   private CourseBookingResultDetails getCourseBookingResultDetails(CourseBookingEndResult courseBookingResultDetails,
                                                                    BookingContext bookingContext) {
      CourseBookResult courseBookResult = mapToCourseBookResult(bookingContext, courseBookingResultDetails.getCourseClickedResult());
      String errorMsg = mapToErrorMessage(courseBookingResultDetails);
      return CourseBookingResultDetailsImpl.of(courseBookResult, courseBookingResultDetails.getCourseName(), errorMsg);
   }

   private static String mapToErrorMessage(CourseBookingEndResult courseBookingEndResult) {
      String errorMsg = String.join("\n", courseBookingEndResult.errors());
      if (courseBookingEndResult.exception() != null) {
         errorMsg = errorMsg + "\n" + courseBookingEndResult.exception().getMessage();
      }
      return errorMsg;
   }

   private static CourseBookResult mapToCourseBookResult(BookingContext bookingContext, CourseClickedResult courseBookResult) {
      if (!bookingContext.dryRun()) {
         return switch (courseBookResult) {
            case COURSE_BOOKED -> CourseBookResult.BOOKED;
            case COURSE_BOOKING_SKIPPED -> CourseBookResult.BOOKING_SKIPPED;
            case COURSE_NOT_BOOKABLE_FULLY_BOOKED -> CourseBookResult.NOT_BOOKED_COURSE_FULLY_BOOKED;
            case COURSE_NOT_BOOKABLE,
                    COURSE_NOT_BOOKED_RETRY,
                    COURSE_NOT_SELECTED_NO_SINGLE_RESULT -> CourseBookResult.NOT_BOOKED_TECHNICAL_ERROR;
            case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED -> CourseBookResult.NOT_BOOKED_EXCEPTION_OCCURRED;
            case COURSE_BOOKING_ABORTED -> CourseBookResult.NOT_BOOKED_UNEXPECTED_ERROR; // should never happen here
         };
      } else {
         return switch (courseBookResult) {
            case COURSE_BOOKING_ABORTED -> CourseBookResult.DRY_RUN_SUCCESSFUL;
            case COURSE_BOOKING_SKIPPED -> CourseBookResult.BOOKING_SKIPPED;
            case COURSE_NOT_BOOKABLE,
                    COURSE_NOT_BOOKED_RETRY,
                    COURSE_NOT_SELECTED_EXCEPTION_OCCURRED,
                    COURSE_NOT_BOOKABLE_FULLY_BOOKED,
                    COURSE_BOOKED,
                    COURSE_NOT_SELECTED_NO_SINGLE_RESULT -> CourseBookResult.DRY_RUN_FAILED;
         };
      }
   }

   @Override
   public List<Course> getBookedCourses() {
      throw new IllegalStateException("Not implemented!");
   }

   @Override
   public CourseCancelResult cancelCourses(String bookingId) {
      throw new IllegalStateException("Not implemented!");
   }
}
