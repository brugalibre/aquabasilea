package com.aquabasilea.migrosapi.api.service;

import com.aquabasilea.migrosapi.api.service.configuration.MigrosApiBookStrategy;
import com.aquabasilea.migrosapi.api.service.configuration.ServiceConfiguration;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.AutoRenewBearerTokenProvider;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenValidator;
import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.service.RetryBookMigrosApi;
import com.aquabasilea.migrosapi.service.security.bearertoken.AutoRenewBearerTokenProviderImpl;
import com.aquabasilea.migrosapi.service.security.bearertoken.BearerTokenValidatorImpl;
import com.brugalibre.common.http.service.HttpService;
import okhttp3.Interceptor;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The {@link MigrosServiceFactory} is used as a version independent api entry point in order to create migros-api
 * related services such as {@link MigrosApi}, {@link BearerTokenValidator} and {@link AutoRenewBearerTokenProvider}.
 * This instance capsules access to actual implementation classes and ensures that only one {@link HttpService} is created
 * for all migros-api related objects
 */
public class MigrosServiceFactory {

   /**
    * Singleton instance of the factory
    */
   public static final MigrosServiceFactory INSTANCE = new MigrosServiceFactory();
   private static final Logger LOG = LoggerFactory.getLogger(MigrosServiceFactory.class);
   private static final int TIME_OUT = 30;
   private HttpService httpService;
   private BearerTokenValidator bearerTokenProvider;
   private MigrosApi migrosApi;

   private MigrosServiceFactory() {
      // private
   }

   /**
    * Creates a new or returns the already created {@link MigrosApi} in v1 with the given {@link BearerTokenProvider}
    *
    * @param bearerTokenProvider  the {@link BearerTokenProvider} responsible for authentication
    * @param serviceConfiguration the {@link ServiceConfiguration}
    * @return a new created {@link MigrosApi}
    */
   public MigrosApi getMigrosApiV1(BearerTokenProvider bearerTokenProvider, ServiceConfiguration serviceConfiguration) {
      LOG.info("Get a MigrosApi in version 1");
      if (this.migrosApi == null) {
         this.migrosApi = createMigrosApiV1(bearerTokenProvider, serviceConfiguration);
      }
      return this.migrosApi;
   }

   private MigrosApi createMigrosApiV1(BearerTokenProvider bearerTokenProvider, ServiceConfiguration serviceConfiguration) {
      MigrosApiBookStrategy migrosApiBookStrategy = serviceConfiguration.migrosApiBookStrategy();
      LOG.info("Creating new MigrosApi with strategy {}", migrosApiBookStrategy);
      checkHttpService(serviceConfiguration);
      return switch (migrosApiBookStrategy) {
         case RETRY -> new RetryBookMigrosApi(new MigrosApiImpl(bearerTokenProvider, httpService));
         case NO_RETRY -> new MigrosApiImpl(bearerTokenProvider, httpService);
      };
   }

   /**
    * Creates a new {@link MigrosApi} in v1 with the given {@link BearerTokenProvider}
    *
    * @param bearerTokenProvider  the {@link BearerTokenProvider} responsible for authentication
    * @param serviceConfiguration the {@link ServiceConfiguration}
    * @param ttl                  the time-to-life of the bearer token, provided by the given {@link BearerTokenProvider}
    * @return a new created {@link AutoRenewBearerTokenProvider}
    */
   public AutoRenewBearerTokenProvider createAutoRenewBearerTokenProviderV1(BearerTokenProvider bearerTokenProvider,
                                                                            ServiceConfiguration serviceConfiguration, int ttl) {
      LOG.info("Creating new AutoRenewBearerTokenProvider with bearerTokenProvider={} and ttl={}", bearerTokenProvider, ttl);
      return new AutoRenewBearerTokenProviderImpl(bearerTokenProvider, ttl, getBearerTokenValidatorV1(serviceConfiguration));
   }

   private BearerTokenValidator getBearerTokenValidatorV1(ServiceConfiguration serviceConfiguration) {
      LOG.info("Get a BearerTokenValidator in version 1");
      if (this.bearerTokenProvider == null) {
         this.bearerTokenProvider = createBearerTokenValidatorV1(serviceConfiguration);
      }
      return this.bearerTokenProvider;
   }

   private BearerTokenValidator createBearerTokenValidatorV1(ServiceConfiguration serviceConfiguration) {
      checkHttpService(serviceConfiguration);
      LOG.info("Creating new BearerTokenValidatorV1");
      return new BearerTokenValidatorImpl(httpService);
   }

   private void checkHttpService(ServiceConfiguration serviceConfiguration) {
      if (httpService == null) {
         this.httpService = createHttpService(serviceConfiguration);
      }
   }

   private static HttpService createHttpService(ServiceConfiguration serviceConfiguration) {
      LOG.info("Creating new HttpService with configuration={}", serviceConfiguration);
      Interceptor headerInterceptor = getHeaderInterceptor(serviceConfiguration.headers());
      return new HttpService(TIME_OUT, headerInterceptor);
   }

   private static Interceptor getHeaderInterceptor(Map<String, String> headers) {
      return chain -> {
         Request request = chain.request();
         for (Map.Entry<String, String> headerKeyValue : headers.entrySet()) {
            request = request.newBuilder()
                    .header(headerKeyValue.getKey(), headerKeyValue.getValue())
                    .build();
         }
         return chain.proceed(request);
      };
   }
}
