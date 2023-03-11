package com.aquabasilea.web.bookcourse.impl.select.result;

public enum CourseClickedResult {
   /**
    * Course is not bookable
    */
   COURSE_NOT_BOOKABLE,

   /**
    * Course is not bookable, because there is no space left
    */
   COURSE_NOT_BOOKABLE_FULLY_BOOKED,

   /**
    * Course successfully booked
    */
   COURSE_BOOKED,

   /**
    * Course not bookable yet, but since we are to early lets try again
    */
   COURSE_NOT_BOOKED_RETRY,

   /**
    * No course selected at all since the course could not be
    * filtered exactly
    */
   COURSE_NOT_SELECTED_NO_SINGLE_RESULT,

   /**
    * The attempt to book a course was aborted
    */
   COURSE_BOOKING_ABORTED,

   /**
    * The attempt to book a course was skipped.
    * Possible reason is, that the Course is not bookable since there is no CourseDef
    */
   COURSE_BOOKING_SKIPPED,

   /**
    * No course selected, since an exception has occurred
    */
   COURSE_NOT_SELECTED_EXCEPTION_OCCURRED,
}
