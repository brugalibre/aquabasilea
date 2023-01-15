package com.aquabasilea.coursebooker.states.booking.consumer;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;

/**
 * Represents a user for which a {@link CourseBookingEndResult} is consumed
 *
 * @param userId  the id of the user, is always set
 * @param phoneNr the phone-nr of the user, may be null
 */
public record ConsumerUser(String userId, String phoneNr) {
   /**
    * Creates a new {@link ConsumerUser} from the given {@link com.aquabasilea.coursebooker.AquabasileaCourseBooker.UserContext}
    *
    * @param userContext the user-context
    * @return a new {@link ConsumerUser} without any phone-nr
    */
   public static ConsumerUser of(AquabasileaCourseBooker.UserContext userContext) {
      return new ConsumerUser(userContext.id(), userContext.phoneNr());
   }

   /**
    * Creates a new {@link ConsumerUser} from the given {@link com.aquabasilea.coursebooker.AquabasileaCourseBooker.UserContext}
    *
    * @param id the technical id of the user
    * @param phoneNr the phone-nr of the user
    * @return a new {@link ConsumerUser} without any phone-nr
    */
   public static ConsumerUser of(String id, String phoneNr) {
      return new ConsumerUser(id, phoneNr);
   }
}
