package com.aquabasilea.application.security.model;

import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;

/**
 * {@link UserContext} is used to provide the {@link AquabasileaCourseBooker} an username and password for browser authentication
 *
 * @param id              the id
 * @param phoneNr         the phoneNr
 */
public record UserContext(String id, String phoneNr) {
   @Override
   public String toString() {
      return "user-id=" + id;
   }
}
