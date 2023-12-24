package com.aquabasilea.migrosapi.api.service.configuration;

import com.aquabasilea.migrosapi.api.service.MigrosServiceFactory;
import com.aquabasilea.migrosapi.api.v1.model.MigrosApiV1Const;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.Map;

/**
 * The {@link ServiceConfiguration} is used as a configuration for the {@link MigrosServiceFactory}
 *
 * @param headers               static headers
 * @param migrosApiBookStrategy a strategy used to create a {@link MigrosApi}
 */
public record ServiceConfiguration(Map<String, String> headers,
                                   MigrosApiBookStrategy migrosApiBookStrategy) {
   /**
    * Creates a {@link ServiceConfiguration} with {@link MigrosApiV1Const#HEADERS} as additional headers as well as
    * the given strategy
    *
    * @param migrosApiBookStrategy the desired {@link MigrosApiBookStrategy}
    * @return a new {@link ServiceConfiguration}
    */
   public static ServiceConfiguration of(MigrosApiBookStrategy migrosApiBookStrategy) {
      return of(MigrosApiV1Const.HEADERS, migrosApiBookStrategy);
   }

   /**
    * Creates a {@link ServiceConfiguration} with the given additional headers as well as the given strategy
    *
    * @param headers               the additional headers
    * @param migrosApiBookStrategy the desired {@link MigrosApiBookStrategy}
    * @return a new {@link ServiceConfiguration}
    */
   public static ServiceConfiguration of(Map<String, String> headers, MigrosApiBookStrategy migrosApiBookStrategy) {
      return new ServiceConfiguration(headers, migrosApiBookStrategy);
   }
}
