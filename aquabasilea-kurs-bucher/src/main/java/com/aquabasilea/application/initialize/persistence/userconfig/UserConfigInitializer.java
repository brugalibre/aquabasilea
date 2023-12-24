package com.aquabasilea.application.initialize.persistence.userconfig;

import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.brugalibre.domain.user.service.userrole.UserRoleConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aquabasilea.application.initialize.common.InitializationConst.USER_CONFIG;

@Service
@InitializeOrder(order = USER_CONFIG, type = {InitType.USER_ADDED, InitType.USER_ACTIVATED})
public class UserConfigInitializer implements InitializerForUser {

   private final UserRoleConfigService userRoleConfigService;

   @Autowired
   public UserConfigInitializer(UserRoleConfigService userRoleConfigService) {
      this.userRoleConfigService = userRoleConfigService;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      userRoleConfigService.addMissingRoles(userAddedEvent.userId());
   }
}
