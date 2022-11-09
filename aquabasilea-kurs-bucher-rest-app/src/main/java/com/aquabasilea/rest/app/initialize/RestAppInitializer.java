package com.aquabasilea.rest.app.initialize;

import com.aquabasilea.app.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.rest.api.security.AquabasileaUserRegisteredObserver;
import com.brugalibre.common.security.rest.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * The {@link RestAppInitializer} registers a {@link AquabasileaUserRegisteredObserver} as soon as the rest-server is started
 * The {@link AquabasileaUserRegisteredObserver} then handles each registration of a user individually
 */
@Service
public class RestAppInitializer {

   private final UserRegisterService userRegisterService;
   private final AquabasileaAppInitializer aquabasileaAppInitializer;

   @Autowired
   public RestAppInitializer(UserRegisterService userRegisterService, AquabasileaAppInitializer aquabasileaAppInitializer) {
      this.userRegisterService = userRegisterService;
      this.aquabasileaAppInitializer = aquabasileaAppInitializer;
   }

   @EventListener
   public void onApplicationEvent(final ServletWebServerInitializedEvent event /*unused*/) {
      createAndAddUserRegisteredObserver();
      aquabasileaAppInitializer.initialize4ExistingUsers();
   }

   private void createAndAddUserRegisteredObserver() {
      AquabasileaUserRegisteredObserver aquabasileaUserRegisteredObserver = new AquabasileaUserRegisteredObserver(aquabasileaAppInitializer);
      userRegisterService.addUserRegisteredObserver(aquabasileaUserRegisteredObserver);
   }
}
