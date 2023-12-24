package com.aquabasilea.application.initialize.api;

import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.brugalibre.domain.user.model.User;

import java.util.Comparator;

/**
 * An {@link AppInitializer} initializes a single part of the application
 */
public interface AppInitializer {
   /**
    * Is called in order to initialize the application for existing {@link User}s, e.g. after the application server
    * was started
    */
   void initializeOnAppStart();

   /**
    * @return a {@link Comparator} which compares two {@link InitializerForUser}s annotated with a
    * {@link InitializeOrder} according their order
    */
   static Comparator<AppInitializer> compareOrder() {
      return (initializer1, initializer2) -> {
         InitializeOrder initializeOrder1 = initializer1.getClass().getAnnotation(InitializeOrder.class);
         InitializeOrder initializeOrder2 = initializer2.getClass().getAnnotation(InitializeOrder.class);
         return Integer.compare(initializeOrder1.order(), initializeOrder2.order());
      };
   }
}
