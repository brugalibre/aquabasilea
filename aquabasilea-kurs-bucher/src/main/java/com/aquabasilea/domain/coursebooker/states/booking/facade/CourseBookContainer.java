package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

/**
 * A {@link BookingContext} combines the {@link CourseBookDetails} as well as the {@link BookingContext }
 *
 * @param courseBookDetails the details about the course to book like name, course location and so on
 * @param bookingContext    the context in which the booking is executed
 */
public record CourseBookContainer(CourseBookDetails courseBookDetails, BookingContext bookingContext) {
}
