package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros;

import com.aquabasilea.application.security.service.AuthenticationContainerService;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link AuthenticationContainerRegistry} serves as a cache for {@link AuthenticationContainer}.
 * Via the {@link AuthenticationContainerService} a new {@link com.aquabasilea.application.security.model.AuthenticationContainer}
 * is retrieved and mapped into the migros specific {@link AuthenticationContainer}.
 * For the migros service the same {@link AuthenticationContainer} is needed for each request. Otherwise the caching of
 * the bearer-token will not work
 */
public final class AuthenticationContainerRegistry {

   private final Map<String, AuthenticationContainer> authenticationContainerToUserIdMap;
   private final AuthenticationContainerService authenticationContainerService;

   public AuthenticationContainerRegistry(AuthenticationContainerService authenticationContainerService) {
      this.authenticationContainerToUserIdMap = new HashMap<>();
      this.authenticationContainerService = authenticationContainerService;
   }

   public AuthenticationContainer getAuthenticationContainerForUserId(String userId) {
      authenticationContainerToUserIdMap.computeIfAbsent(userId, this::getAndMapAuthenticationContainer);
      return authenticationContainerToUserIdMap.get(userId);
   }

   private AuthenticationContainer getAndMapAuthenticationContainer(String userId) {
      com.aquabasilea.application.security.model.AuthenticationContainer authenticationContainer = this.authenticationContainerService.getAuthenticationContainer(userId);
      return new AuthenticationContainer(authenticationContainer.username(), authenticationContainer.userPwdSupplier());
   }
}
