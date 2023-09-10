package com.aquabasilea.migrosapi.v1.model.book;

/**
 * A request in order to cancel a previously booked course
 *
 * @param courseBookingId the id which defines the arrangement to cancel
 */
public record MigrosApiCancelCourseRequest(String courseBookingId) {

    public static MigrosApiCancelCourseRequest of(String courseBookingId) {
        return new MigrosApiCancelCourseRequest(courseBookingId);
    }
}
