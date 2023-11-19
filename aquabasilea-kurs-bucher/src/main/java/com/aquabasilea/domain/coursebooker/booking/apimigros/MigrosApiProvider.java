package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.coursebooker.booking.apimigros.security.AquabasileaWebBearerTokenProviderImpl;
import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.v1.service.security.BearerTokenValidator;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.AutoRenewBearerTokenProvider;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@Service
public class MigrosApiProvider {
   private final Supplier<MigrosApi> migrosApiSup;

   @Autowired
   public MigrosApiProvider(@Value("${application.security.bearerTokenTtl:0}") int ttl,
                            @Value("${application.configuration.course-booker-config}") String propertiesFile) {
      BearerTokenProvider bearerTokenProvider = new AutoRenewBearerTokenProvider(
              new AquabasileaWebBearerTokenProviderImpl(propertiesFile), ttl, new BearerTokenValidator());
      this.migrosApiSup = () -> new MigrosApiImpl(bearerTokenProvider);
   }

   public MigrosApiProvider(MigrosApi migrosApi) {
      this.migrosApiSup = () -> requireNonNull(migrosApi);
   }

   public MigrosApi getNewMigrosApi() {
      return migrosApiSup.get();
   }
}
