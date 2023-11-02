package com.aquabasilea.rest.app.initialize;

import com.aquabasilea.application.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.rest.api.security.AquabasileaUserRegisteredObserver;
import com.aquabasilea.rest.api.user.change.UserChangedObserverImpl;
import com.brugalibre.common.security.rest.service.UserRegisterService;
import com.brugalibre.common.security.rest.service.passwordchange.UserUserPasswordChangeService;
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
   private final UserUserPasswordChangeService userUserPasswordChangeService;
   private final AquabasileaAppInitializer aquabasileaAppInitializer;
   private final UserChangedObserverImpl userChangedObserver;

   @Autowired
   public RestAppInitializer(UserRegisterService userRegisterService, UserUserPasswordChangeService userUserPasswordChangeService,
                             AquabasileaAppInitializer aquabasileaAppInitializer, UserChangedObserverImpl userChangedObserver) {
      this.userRegisterService = userRegisterService;
      this.userChangedObserver = userChangedObserver;
      this.aquabasileaAppInitializer = aquabasileaAppInitializer;
      this.userUserPasswordChangeService = userUserPasswordChangeService;
   }

   @EventListener
   @SuppressWarnings("unused")
   public void onApplicationEvent(final ServletWebServerInitializedEvent event /*unused*/) {
      createAndAddUserRegisteredObserver();
      userUserPasswordChangeService.addUserPasswordChangedObserver(userChangedObserver);
      aquabasileaAppInitializer.initializeOnAppStart();
   }

   private void createAndAddUserRegisteredObserver() {
      AquabasileaUserRegisteredObserver aquabasileaUserRegisteredObserver = new AquabasileaUserRegisteredObserver(aquabasileaAppInitializer);
      userRegisterService.addUserRegisteredObserver(aquabasileaUserRegisteredObserver);
   }
}
