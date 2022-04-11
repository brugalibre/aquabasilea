package com.aquabasilea.coursebooker.callback;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;

import java.util.function.Supplier;

/**
 * {@link AuthenticationCallbackHandler} is used by the {@link AquabasileaCourseBooker} to handle the authentication of a user
 * in a external system
 */
public interface AuthenticationCallbackHandler {

   /**
    * Is called as soon as a user gets authenticated
    *
    * @param username        the username
    * @param userPwdSupplier the Supplier for the users password
    */
   void onUserAuthenticated(String username, Supplier<char[]> userPwdSupplier);
}
