package com.aquabasilea.app.initialize.api;


import com.aquabasilea.app.initialize.Initializer;
import com.brugalibre.domain.user.model.User;

/**
 * The {@link AquabasileaAppInitializer} is a public {@link Initializer} for the entire application scope
 */
public interface AquabasileaAppInitializer extends Initializer {
   /**
    * Is called in order to initialize the application for existing {@link User}s, e.g. after the application server
    * was started
    */
   void initialize4ExistingUsers();
}
