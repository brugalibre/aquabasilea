package com.zeiterfassung.web.aquabasilea.login;

/**
 * The {@link LoginCallback} hides the actual implementation of a login
 */
@FunctionalInterface
public interface LoginCallback {

   void login();
}
