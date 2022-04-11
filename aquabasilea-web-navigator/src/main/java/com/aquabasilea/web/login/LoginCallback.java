package com.aquabasilea.web.login;

/**
 * The {@link LoginCallback} hides the actual implementation of a login
 */
@FunctionalInterface
public interface LoginCallback {

   void login();
}
