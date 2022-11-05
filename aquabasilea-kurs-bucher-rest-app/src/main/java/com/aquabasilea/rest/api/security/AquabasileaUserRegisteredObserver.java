package com.aquabasilea.rest.api.security;

import com.aquabasilea.security.securestorage.WriteSecretToKeyStore;
import com.brugalibre.common.security.auth.register.UserRegisteredEvent;
import com.brugalibre.common.security.auth.register.UserRegisteredObserver;
import com.brugalibre.common.security.rest.model.RegisterRequest;

public class AquabasileaUserRegisteredObserver implements UserRegisteredObserver {

   public static final String AQUABASILEA_KEYSTORE = "aquabasilea.keystore";

   @Override
   public void userRegistered(UserRegisteredEvent userRegisteredEvent) {
      RegisterRequest registerRequest = userRegisteredEvent.registerRequest();
      new WriteSecretToKeyStore().writeSecretToKeyStore(AQUABASILEA_KEYSTORE, "Aqua21!basilea22^^".toCharArray(), registerRequest.username(), registerRequest.password().toCharArray());
   }
}
