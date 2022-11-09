package com.aquabasilea.app.initialize.api;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.brugalibre.domain.user.model.User;

import java.util.Arrays;

/**
 * Tells the {@link AquabasileaCourseBooker} that a new {@link User} was added so that all necessary
 * preparations can be done
 *
 * @param username the username of the user
 * @param phoneNr  the phone-Nr of the user
 * @param userId   the technical id of the {@link User}
 * @param password the users password
 */
public record UserAddedEvent(String username, String phoneNr, String userId, char[] password) {
   /**
    * Creates a new {@link UserAddedEvent} of the given {@link User}
    *
    * @param user the {@link User}
    * @return a new {@link UserAddedEvent} of the given {@link User}
    */
   public static UserAddedEvent of(User user) {
      return new UserAddedEvent(user.username(), user.phoneNr(), user.id(), null);
   }

   @Override
   public String toString() {
      return "UserAddedEvent{" +
              "username='" + "PROTECTED" + '\'' +
              ", phoneNr='" + phoneNr + '\'' +
              ", userId='" + userId + '\'' +
              ", password=" + "PROTECTED" +
              '}';
   }
}
