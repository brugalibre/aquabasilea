package com.aquabasilea.migrosapi.v1.service;

import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.service.RetryBookMigrosApi;
import com.aquabasilea.migrosapi.service.security.bearertoken.BearerTokenValidatorImpl;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenValidator;
import com.brugalibre.common.http.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MigrosServiceFactory} is used as an api entry point in order to create migros-api related services such as
 * {@link MigrosApi} and {@link BearerTokenValidator}.
 * This instance ensures that only one {@link HttpService} is created for all migros-api related objects
 */
public class MigrosServiceFactory {

   /**
    * Singleton instance of the factory
    */
   public static final MigrosServiceFactory INSTANCE = new MigrosServiceFactory();
   private static final Logger LOG = LoggerFactory.getLogger(MigrosServiceFactory.class);
   private static final int TIME_OUT = 30;
   private final HttpService httpService;

   private MigrosServiceFactory() {
      this.httpService = new HttpService(TIME_OUT);
   }

   /**
    * Creates a new {@link MigrosApi} with the given {@link BearerTokenProvider}
    *
    * @param bearerTokenProvider   the {@link BearerTokenProvider} responsible for authentication
    * @param migrosApiBookStrategy the {@link MigrosApiBookStrategy} which defines the behaviour of the returned {@link MigrosApi}
    * @return a new created {@link MigrosApi}
    */
   public MigrosApi createNewMigrosApi(BearerTokenProvider bearerTokenProvider, MigrosApiBookStrategy migrosApiBookStrategy) {
      LOG.info("Creating new MigrosApi with strategy {}", migrosApiBookStrategy);
      return switch (migrosApiBookStrategy) {
         case RETRY -> new RetryBookMigrosApi(new MigrosApiImpl(bearerTokenProvider, httpService));
         case NO_RETRY -> new MigrosApiImpl(bearerTokenProvider, httpService);
      };
   }

   /**
    * @return a new created {@link BearerTokenValidator}
    */
   public BearerTokenValidator createNewBearerTokenValidator() {
      LOG.info("Creating new BearerTokenValidator");
      return new BearerTokenValidatorImpl(httpService);
   }

   public enum MigrosApiBookStrategy {
      /**
       * No retry
       */
      NO_RETRY,
      /**
       * Retry on certain, retryable, errors
       */
      RETRY,
   }
}
