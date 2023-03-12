package com.aquabasilea.migrosapi.service.security;

import com.aquabasilea.migrosapi.service.security.api.BearerTokenProvider;

import java.util.function.Supplier;

public class MigrosApiBearerTokenProviderImpl implements BearerTokenProvider {
   @Override
   public String getBearerToken(String username, Supplier<char[]> userPwd) {
      // Not yet implemented..
      return null;
   }
}
