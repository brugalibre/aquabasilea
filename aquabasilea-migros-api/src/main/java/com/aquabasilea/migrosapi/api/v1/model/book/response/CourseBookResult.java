package com.aquabasilea.migrosapi.api.v1.model.book.response;

public enum CourseBookResult {

   /**
    * Course successfully booked
    */
   COURSE_BOOKED(0),

   /**
    * Course is not bookable, because there is no space left
    */
   COURSE_NOT_BOOKABLE_FULLY_BOOKED(1),

   /**
    * Course is not bookable, because it was already booked for the same user earlier
    */
   COURSE_NOT_BOOKABLE_ALREADY_BOOKED(2),

   /**
    * Course is not bookable, an unknown, migros-api internal, error occurred
    */
   COURSE_NOT_BOOKABLE_TECHNICAL_ERROR(3),

   /**
    * Course is not bookable, there was an unexpected error on the rest-api
    */
   COURSE_NOT_BOOKED_UNEXPECTED_ERROR(99),

   /**
    * No course selected, since an exception has occurred
    */
   COURSE_NOT_SELECTED_EXCEPTION_OCCURRED(999),

   /**
    * The dry run was successful
    */
   COURSE_BOOKING_DRY_RUN_SUCCESSFUL(-1),

   /**
    * The dry run was <b>not</b> successful
    */
   COURSE_BOOKING_DRY_RUN_FAILED(-99);

   private final int errorCode;

   CourseBookResult(int errorCode) {
      this.errorCode = errorCode;
   }

   public int getErrorCode() {
      return errorCode;
   }
}
