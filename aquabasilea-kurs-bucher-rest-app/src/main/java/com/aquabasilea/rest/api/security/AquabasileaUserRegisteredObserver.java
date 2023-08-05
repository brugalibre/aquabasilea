package com.aquabasilea.rest.api.security;

import com.aquabasilea.application.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.brugalibre.common.security.auth.register.UserRegisteredEvent;
import com.brugalibre.common.security.auth.register.UserRegisteredObserver;
import com.brugalibre.common.security.user.model.User;

/**
 * The {@link AquabasileaUserRegisteredObserver} handles as a {@link UserRegisteredObserver} the event of a new registration
 * of a {@link User} It therefore forwards the event to the {@link AquabasileaAppInitializer} which creates and initializes
 * all necessary components for the new user
 *
 * @param aquabasileaAppInitializer the {@link AquabasileaAppInitializer}
 */
public record AquabasileaUserRegisteredObserver(
        AquabasileaAppInitializer aquabasileaAppInitializer) implements UserRegisteredObserver {

   @Override
   public void userRegistered(UserRegisteredEvent userRegisteredEvent) {
      aquabasileaAppInitializer.initialize(createUserAddedEvent(userRegisteredEvent));
   }

   private static UserAddedEvent createUserAddedEvent(UserRegisteredEvent userRegisteredEvent) {
      return new UserAddedEvent(userRegisteredEvent.username(), userRegisteredEvent.phoneNr(),
              userRegisteredEvent.userId(), userRegisteredEvent.password());
   }
}
