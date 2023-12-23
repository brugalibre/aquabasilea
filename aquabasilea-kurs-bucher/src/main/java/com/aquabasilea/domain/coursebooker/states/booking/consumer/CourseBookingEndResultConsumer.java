package com.aquabasilea.domain.coursebooker.states.booking.consumer;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;

public interface CourseBookingEndResultConsumer {

   /**
    * Consumes the given result from the course-booking
    *
    * @param consumerUser               the {@link ConsumerUser} for which the {@link CourseBookingEndResult} is consumed
    * @param courseBookingResultDetails the {@link CourseBookingResultDetails}
    * @param courseBookingState         defines weather or not it was a dry-run or a real booking
    */
   void consumeResult(ConsumerUser consumerUser, CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState);
}
