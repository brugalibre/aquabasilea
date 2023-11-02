package com.aquabasilea.application.initialize.common;

import com.aquabasilea.application.initialize.api.Initializer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
public @interface InitializeOrder {
   /**
    * @return the order of the {@link Initializer}
    */
   int order();

   /**
    * The type of this {@link Initializer} since certain {@link Initializer} are only necessary when a new user was
    * registered
    *
    * @return the {@link InitType}
    */
   InitType[] type();
}
