package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.security;

import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The {@link MultiUserBearerTokenProvider} is a {@link BearerTokenProvider} which internally creates
 * for each user, which requests the bearer-token, a new {@link BearerTokenProvider}.
 * This BearerTokenProvider is created, or supplied, by a {@link Supplier} which is required in order to create a {@link MultiUserBearerTokenProvider}
 */
public class MultiUserBearerTokenProvider implements BearerTokenProvider {

   private static final Logger LOG = LoggerFactory.getLogger(MultiUserBearerTokenProvider.class);
   private final Supplier<BearerTokenProvider> newBearerTokenProviderSupplier;
   private final Map<String, BearerTokenProvider> userNameToBearerTokenProviderMap;

   public MultiUserBearerTokenProvider(Supplier<BearerTokenProvider> newBearerTokenProviderSupplier) {
      this.newBearerTokenProviderSupplier = newBearerTokenProviderSupplier;
      this.userNameToBearerTokenProviderMap = new HashMap<>();
   }

   @Override
   public String getBearerToken(String username, Supplier<char[]> userPwd) {
      BearerTokenProvider bearerTokenProvider = getBearerTokenProvider4Username(username);
      return bearerTokenProvider.getBearerToken(username, userPwd);
   }

   private BearerTokenProvider getBearerTokenProvider4Username(String username) {
      if (userNameToBearerTokenProviderMap.containsKey(username)) {
         return userNameToBearerTokenProviderMap.get(username);
      }
      LOG.info("Create new bearer-token provider");
      BearerTokenProvider bearerTokenProvider = newBearerTokenProviderSupplier.get();
      userNameToBearerTokenProviderMap.put(username, bearerTokenProvider);
      return bearerTokenProvider;
   }
}
