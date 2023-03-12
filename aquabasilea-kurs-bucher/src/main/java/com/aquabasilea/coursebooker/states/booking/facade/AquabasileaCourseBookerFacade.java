
package com.aquabasilea.coursebooker.states.booking.facade;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

/**
 * The {@link AquabasileaCourseBookerFacade} is a facade hiding the implementation of the actual booking process
 * like either a web based or a migros-rest-api based implementation
 */
public interface AquabasileaCourseBookerFacade {
   /**
    * Books the course defined by the given {@link CourseBookDetails}
    *
    * @param courseBookContainer the {@link CourseBookContainer} with {@link CourseBookDetails} and a {@link BookingContext}
    * @return a {@link CourseBookingEndResult} describing the outcome of the course booking
    */
   CourseBookingEndResult selectAndBookCourse(CourseBookContainer courseBookContainer);
}
