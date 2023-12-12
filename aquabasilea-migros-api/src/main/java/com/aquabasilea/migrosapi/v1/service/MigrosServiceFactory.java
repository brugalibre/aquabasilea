package com.aquabasilea.migrosapi.v1.service;

import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.service.security.bearertoken.BearerTokenValidatorImpl;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenValidator;
import com.brugalibre.common.http.service.HttpService;

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
   private static final int TIME_OUT = 30;
   private final HttpService httpService;

   private MigrosServiceFactory() {
      this.httpService = new HttpService(TIME_OUT);
   }

   /**
    * Creates a new {@link MigrosApi} with the given {@link BearerTokenProvider}
    *
    * @param bearerTokenProvider the {@link BearerTokenProvider} responsible for authentication
    * @return a new created {@link MigrosApi}
    */
   public MigrosApi createNewMigrosApi(BearerTokenProvider bearerTokenProvider) {
      return new MigrosApiImpl(bearerTokenProvider, httpService);
   }

   /**
    * @return a new created {@link BearerTokenValidator}
    */
   public BearerTokenValidator createNewBearerTokenValidator() {
      return new BearerTokenValidatorImpl(httpService);
   }
}
