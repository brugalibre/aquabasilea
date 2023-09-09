package com.aquabasilea.migrosapi.v1.service.security;


import java.util.function.Supplier;

/**
 * The {@link BearerTokenProvider} provides the bearer token for a given combination of username and users password
 */
public interface BearerTokenProvider {

   /**
    * Evaluates the bearer token for the given authentication container
    *
    * @param username the username
    * @param userPwd  a supplier for the users password
    * @return the bearer token for the given authentication container
    */
   String getBearerToken(String username, Supplier<char[]> userPwd);
}
