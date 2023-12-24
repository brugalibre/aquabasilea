
package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

import java.util.List;

/**
 * The {@link CourseBookerFacade} is a facade hiding the implementation of the actual booking process
 * like either a web based or a migros-rest-api based implementation
 */
public interface CourseBookerFacade extends CourseDefExtractorFacade {
   /**
    * Books the course defined by the given {@link CourseBookDetails}
    *
    * @param courseBookContainer the {@link CourseBookContainer} with {@link CourseBookDetails} and a {@link BookingContext}
    * @return a {@link CourseBookingEndResult} describing the outcome of the course booking
    */
   CourseBookingResultDetails bookCourse(CourseBookContainer courseBookContainer);

   /**
    * @return a {@link List} of {@link Course}s Objects for which each represents a booked course
    */
   List<Course> getBookedCourses();

   /**
    * Cancel a previously booked {@link Course} for the given booking-id
    *
    * @param bookingId the id of the booking which represents the booking arrangement
    * @return a {@link CourseCancelResult} which indicates weather or not the cancel was successful
    */
   CourseCancelResult cancelCourses(String bookingId);
}
