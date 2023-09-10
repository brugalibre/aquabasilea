package com.aquabasilea.migrosapi.v1.model.book.response;

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
    * Course is not bookable
    */
   COURSE_NOT_BOOKED(99),

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
