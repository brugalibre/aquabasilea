package com.zeiterfassung.web.aquabasilea.selectcourse.result;

public enum CourseClickedResult {
   /**
    * Course is not bookable
    */
   COURSE_NOT_BOOKABLE,

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
    * No course selected, since an exception has occurred
    */
   COURSE_NOT_SELECTED_EXCEPTION_OCCURRED,
}
