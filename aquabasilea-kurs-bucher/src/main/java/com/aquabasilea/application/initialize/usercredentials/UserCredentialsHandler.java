package com.aquabasilea.application.initialize.usercredentials;

import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.application.security.securestorage.WriteSecretToKeyStore;
import com.aquabasilea.application.security.service.login.AquabasileaLoginService;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.aquabasilea.application.initialize.common.InitializationConst.USER_CREDENTIALS;

/**
 * The {@link UserCredentialsHandler} stores the given credential in a secure storage for a later use
 * by the {@link AquabasileaWebCourseBooker}. Additionally, those credential are verified by the {@link AquabasileaLoginService}
 * in order to make sure, we are able to log in on the migros-fitness page for booking.
 * The {@link UserCredentialsHandler} is also used if the password was changed by the user, in order to verify if a
 * login is possible with the new password
 */
@Service
@InitializeOrder(order = USER_CREDENTIALS, type = {InitType.USER_ADDED})
public class UserCredentialsHandler implements InitializerForUser {

   private final WriteSecretToKeyStore writeSecretToKeyStore;
   private final AquabasileaLoginService aquabasileaLoginService;

   @Value("${application.security.keyStorePassword}")
   private String keyStorePassword;

   @Value("${application.security.aquabasileaKeyStoreName}")
   private String aquabasileaKeyStoreName;

   @Autowired
   public UserCredentialsHandler(AquabasileaLoginService aquabasileaLoginService) {
      this.writeSecretToKeyStore = new WriteSecretToKeyStore();
      this.aquabasileaLoginService = aquabasileaLoginService;
   }

   public UserCredentialsHandler(AquabasileaLoginService aquabasileaLoginService, String keyStorePassword, String aquabasileaKeyStoreName) {
      this.writeSecretToKeyStore = new WriteSecretToKeyStore();
      this.aquabasileaLoginService = aquabasileaLoginService;
      this.keyStorePassword = keyStorePassword;
      this.aquabasileaKeyStoreName = aquabasileaKeyStoreName;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      String username = userAddedEvent.username();
      char[] userPassword = userAddedEvent.password();
      validateAndStoreUserCredentials(username, userPassword);
   }

   /**
    * Validates the given username and password. This means, an actual login is done by the {@link AquabasileaLoginService}
    * and only if this is successful the password is stored securely in the provided secret-store
    *
    * @param username     the username
    * @param userPassword the password
    */
   public void validateAndStoreUserCredentials(String username, char[] userPassword) {
      char[] passwordCopy = Arrays.copyOf(userPassword, userPassword.length);
      // We have to pass a copy, since the aquabasilea-login resets the forwarded one
      aquabasileaLoginService.validateCredentials(username, passwordCopy);
      // add the user's password to the internal key-store. So that the AquabasileaWebNavigator can do a login
      writeSecretToKeyStore.writeSecretToKeyStore(aquabasileaKeyStoreName, keyStorePassword.toCharArray(),
              username, userPassword);
   }
}
