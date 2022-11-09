package com.aquabasilea.app.initialize;


import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.brugalibre.domain.user.model.User;

/**
 * The {@link Initializer} is used to initialize the app for a certain {@link User}
 */
public interface Initializer {
   /**
    * Initialize a certain part of the app for a new {@link User}
    *
    * @param userAddedEvent the {@link UserAddedEvent} with details about the added user
    */
   void initialize(UserAddedEvent userAddedEvent);
}
