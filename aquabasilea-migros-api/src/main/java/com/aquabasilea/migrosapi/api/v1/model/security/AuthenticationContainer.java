package com.aquabasilea.migrosapi.api.v1.model.security;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * {@link AuthenticationContainer} for authentication any migros-api request
 * It's safe to cache an {@link AuthenticationContainer} since the password value is provided by a {@link Supplier} function
 *
 * @param username        the username
 * @param userPwdSupplier {@link Supplier} which provides the user password
 */
public record AuthenticationContainer(String username, Supplier<char[]> userPwdSupplier) {

   public static AuthenticationContainer of(String username, Supplier<char[]> userPwdSupplier) {
      requireNonNull(username, "username must be set!");
      requireNonNull(userPwdSupplier, "userPwdSupplier must be set!");
      return new AuthenticationContainer(username, userPwdSupplier);
   }
}
