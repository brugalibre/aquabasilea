package com.aquabasilea.coursebooker.consumer;

import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;

public interface CourseBookingEndResultConsumer {

   /**
    * Consumes the given result from the course-booking
    *
    * @param courseBookingEndResult the {@link CourseBookingEndResult}
    * @param courseBookingState     defines weather or not it was a dry-run or a real booking
    */
   void consumeResult(CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState);
}
