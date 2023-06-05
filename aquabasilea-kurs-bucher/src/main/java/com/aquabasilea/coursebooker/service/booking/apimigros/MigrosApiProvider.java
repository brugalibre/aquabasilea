package com.aquabasilea.coursebooker.service.booking.apimigros;

import com.aquabasilea.coursebooker.service.booking.facade.authentication.AquabasileaWebBearerTokenProviderImpl;
import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@Service
public class MigrosApiProvider {
   private final Supplier<MigrosApi> migrosApiSup;

   public MigrosApiProvider() {
      this.migrosApiSup = () -> new MigrosApiImpl(new AquabasileaWebBearerTokenProviderImpl());
   }

   public MigrosApiProvider(MigrosApi migrosApi) {
      this.migrosApiSup = () -> requireNonNull(migrosApi);
   }

   public MigrosApi getNewMigrosApi() {
      return migrosApiSup.get();
   }
}