package com.aquabasilea.app.initialize.usercredentials;

import com.aquabasilea.app.initialize.Initializer;
import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.security.securestorage.WriteSecretToKeyStore;
import com.aquabasilea.security.service.login.AquabasileaLoginService;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * The {@link UserCredentialsInitializer} stores the given credential in a secure storage for a later use
 * by the {@link AquabasileaWebCourseBooker}. Additionally, those credential are verified by the {@link AquabasileaLoginService}
 * in order to make sure, we are able to log in on the migros-fitness page for booking
 */
@Service
public class UserCredentialsInitializer implements Initializer {

   private final WriteSecretToKeyStore writeSecretToKeyStore;
   private final AquabasileaLoginService aquabasileaLoginService;

   @Value("${application.security.keyStorePassword}")
   private String keyStorePassword;

   @Value("${application.security.aquabasileaKeyStoreName}")
   private String aquabasileaKeyStoreName;

   @Autowired
   public UserCredentialsInitializer(AquabasileaLoginService aquabasileaLoginService) {
      this.writeSecretToKeyStore = new WriteSecretToKeyStore();
      this.aquabasileaLoginService = aquabasileaLoginService;
   }

   public UserCredentialsInitializer(AquabasileaLoginService aquabasileaLoginService, String keyStorePassword, String aquabasileaKeyStoreName) {
      this.writeSecretToKeyStore = new WriteSecretToKeyStore();
      this.aquabasileaLoginService = aquabasileaLoginService;
      this.keyStorePassword = keyStorePassword;
      this.aquabasileaKeyStoreName = aquabasileaKeyStoreName;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      char[] passwordCopy = Arrays.copyOf(userAddedEvent.password(), userAddedEvent.password().length);
      // We have to pass a copy, since the aquabasilea-login resets the forwarded one
      aquabasileaLoginService.validateCredentials(userAddedEvent.username(), passwordCopy);
      // add the user's password to the internal key-store. So that the AquabasileaWebNavigator can do a login
      writeSecretToKeyStore.writeSecretToKeyStore(aquabasileaKeyStoreName, keyStorePassword.toCharArray(),
              userAddedEvent.username(), userAddedEvent.password());
   }
}
