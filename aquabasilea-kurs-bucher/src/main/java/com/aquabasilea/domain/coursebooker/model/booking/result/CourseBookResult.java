package com.aquabasilea.domain.coursebooker.model.booking.result;

/**
 * The {@link CourseBookResult} defines the possible outcomes of a course booking
 */
public enum CourseBookResult {

   /**
    * Course successfully booked
    */
   BOOKED(0),

   /**
    * Course was not booked because there is no space left
    */
   NOT_BOOKED_COURSE_FULLY_BOOKED(1),

   /**
    * Course is not bookable, because it was already booked for the same user earlier
    */
   NOT_BOOKED_COURSE_ALREADY_BOOKED(2),

   /**
    * Course was not booked, an unknown, course-booker-api internal, error occurred
    */
   NOT_BOOKED_TECHNICAL_ERROR(3),

   /***
    * Booking was skipped
    */
   BOOKING_SKIPPED(10),

   /**
    * Course was not booked because, there was an unexpected error on the rest-api
    */
   NOT_BOOKED_UNEXPECTED_ERROR(99),

   /**
    * Course was not booked because an exception has occurred
    */
   NOT_BOOKED_EXCEPTION_OCCURRED(999),

   /**
    * The dry run was successful
    */
   DRY_RUN_SUCCESSFUL(-1),

   /**
    * The dry run was <b>not</b> successful
    */
   DRY_RUN_FAILED(-99);

   private final int errorCode;

   CourseBookResult(int errorCode) {
      this.errorCode = errorCode;
   }

   public int getErrorCode() {
      return errorCode;
   }
}
