package com.aquabasilea.migrosapi.v1.service.security.bearertoken;

import com.aquabasilea.migrosapi.v1.service.security.BearerTokenValidator;
import com.brugalibre.util.lazy.TtlLazyValueBiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * The {@link AutoRenewBearerTokenProvider} always returns a valid bearer token. It uses a {@link TtlLazyValueBiFunction}
 * in order to cache a bearer token for a given time-to-life and avoid unnecessary further calls.
 * Anyway if that bearer token becomes invalid (since it's the migros which defines the actual time-out for their tokens)
 * before the ttl is reached, then a new one is retrieved.
 * This new token then is again valid for the time defined by the provided <code>ttl</ttl> during the construction of this {@link AutoRenewBearerTokenProvider}
 */
public class AutoRenewBearerTokenProvider implements BearerTokenProvider {

   private static final Logger LOG = LoggerFactory.getLogger(AutoRenewBearerTokenProvider.class);
   private final TtlLazyValueBiFunction<String, Supplier<char[]>, String> ttlLazyValueBiFunction;
   private final BearerTokenValidator bearerTokenValidator;

   public AutoRenewBearerTokenProvider(BearerTokenProvider bearerTokenProvider, int timeToLife, BearerTokenValidator bearerTokenValidator) {
      this.ttlLazyValueBiFunction = new TtlLazyValueBiFunction<>(timeToLife, bearerTokenProvider::getBearerToken);
      this.bearerTokenValidator = bearerTokenValidator;
   }

   @Override
   public String getBearerToken(String username, Supplier<char[]> userPwSupplier) {
      LOG.info("Token requested");
      String bearerToken = ttlLazyValueBiFunction.apply(username, userPwSupplier);
      if (bearerTokenValidator.isBearerTokenUnauthorized(bearerToken)) {
         LOG.warn("Received token invalid! Renew token");
         return ttlLazyValueBiFunction.applyForced(username, userPwSupplier);
      }
      return bearerToken;
   }
}
