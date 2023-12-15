package com.aquabasilea.service.coursebooker.migros;

import com.aquabasilea.domain.coursebooker.booking.apimigros.security.AquabasileaWebBearerTokenProviderImpl;
import com.aquabasilea.domain.coursebooker.booking.apimigros.security.MultiUserBearerTokenProvider;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.v1.service.MigrosServiceFactory;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.AutoRenewBearerTokenProvider;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.aquabasilea.migrosapi.v1.service.MigrosServiceFactory.MigrosApiBookStrategy.RETRY;
import static java.util.Objects.requireNonNull;

@Service
public class MigrosApiProvider {
   private final MigrosApi migrosApi;

   @Autowired
   public MigrosApiProvider(@Value("${application.security.bearerTokenTtl:0}") int ttl,
                            @Value("${application.configuration.course-booker-config}") String propertiesFile) {
      BearerTokenProvider bearerTokenProvider = getBearerTokenProvider(ttl, propertiesFile);
      this.migrosApi = MigrosServiceFactory.INSTANCE.createNewMigrosApi(bearerTokenProvider, RETRY);
   }

   private static BearerTokenProvider getBearerTokenProvider(int ttl, String propertiesFile) {
      MultiUserBearerTokenProvider bearerTokenProvider = new MultiUserBearerTokenProvider(() -> new AquabasileaWebBearerTokenProviderImpl(propertiesFile));
      return new AutoRenewBearerTokenProvider(bearerTokenProvider, ttl, MigrosServiceFactory.INSTANCE.createNewBearerTokenValidator());
   }

   public MigrosApiProvider(MigrosApi migrosApi) {
      this.migrosApi = requireNonNull(migrosApi);
   }

   public MigrosApi getMigrosApi() {
      return migrosApi;
   }
}
