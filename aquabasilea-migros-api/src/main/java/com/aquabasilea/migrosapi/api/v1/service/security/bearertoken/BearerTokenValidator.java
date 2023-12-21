package com.aquabasilea.migrosapi.api.v1.service.security.bearertoken;

import com.aquabasilea.migrosapi.service.security.bearertoken.BearerTokenValidatorImpl;

/**
 * The {@link BearerTokenValidatorImpl} validates a bearer token.
 */
public interface BearerTokenValidator {

   /**
    * Validates the given bearer token.  It is invalid if and only if the given token is either <code>null</code> or
    * unauthorized, which means that a call to the migros api returns a 401 code!
    *
    * @param bearerToken the bearer token
    * @return <code>true</code> if the given token is invalid or <code>null</code>. Return <code>false</code> if the token
    * is valid and an authorization successful
    */
   boolean isBearerTokenUnauthorized(String bearerToken);
}
