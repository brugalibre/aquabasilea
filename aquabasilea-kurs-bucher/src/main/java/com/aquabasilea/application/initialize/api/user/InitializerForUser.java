package com.aquabasilea.application.initialize.api.user;

import com.brugalibre.domain.user.model.User;

/**
 * The {@link InitializerForUser} is used to initialize the app for a certain {@link User}
 */
public interface InitializerForUser {
   /**
    * Initialize a certain part of the app for a new {@link User}
    *
    * @param userAddedEvent the {@link UserAddedEvent} with details about the added user
    */
   void initialize(UserAddedEvent userAddedEvent);
}
