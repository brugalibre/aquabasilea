package com.aquabasilea.application.initialize;

import com.aquabasilea.application.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.application.initialize.coursebooker.AquabasileaCourseBookerInitializer;
import com.aquabasilea.application.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.application.initialize.usercredentials.UserCredentialsHandler;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import com.brugalibre.domain.user.service.userrole.UserRoleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AquabasileaAppInitializerImpl implements AquabasileaAppInitializer {

   private final static Logger LOG = LoggerFactory.getLogger(AquabasileaAppInitializerImpl.class);
   private final UserCredentialsHandler userCredentialsHandler;
   private final PersistenceInitializer persistenceInitializer;
   private final AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;
   private final CourseDefUpdater courseDefUpdater;
   private final UserRepository userRepository;
   private final UserRoleConfigService userRoleConfigService;

   @Autowired
   public AquabasileaAppInitializerImpl(PersistenceInitializer persistenceInitializer,
                                        UserCredentialsHandler userCredentialsHandler,
                                        AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer,
                                        CourseDefUpdater courseDefUpdater,
                                        UserRepository userRepository,
                                        UserRoleConfigService userRoleConfigService) {
      this.persistenceInitializer = persistenceInitializer;
      this.userCredentialsHandler = userCredentialsHandler;
      this.aquabasileaCourseBookerInitializer = aquabasileaCourseBookerInitializer;
      this.userRoleConfigService = userRoleConfigService;
      this.courseDefUpdater = courseDefUpdater;
      this.userRepository = userRepository;
   }

   /**
    * Initializes the entire Aquabasilea-application including the persistence as well as the {@link AquabasileaCourseBooker}
    * This initialization e.g. happens when a new {@link User} was registered
    *
    * @param userAddedEvent the {@link UserAddedEvent} with details about the added user
    */
   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      LOG.info("Initialize for new user [{}]", userAddedEvent.userId());
      userCredentialsHandler.initialize(userAddedEvent);
      persistenceInitializer.initialize(userAddedEvent);
      courseDefUpdater.startScheduler(userAddedEvent.userId());
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
   }

   @Override
   public void initialize4ExistingUsers() {
      List<User> registeredUsers = userRepository.getAll();
      LOG.info("Going to initialize for total {} users..", registeredUsers.size());
      for (User user : registeredUsers) {
         UserAddedEvent userAddedEvent = UserAddedEvent.of(user);
         courseDefUpdater.startScheduler(userAddedEvent.userId());
         aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
         userRoleConfigService.addMissingRoles(user.id());
      }
      LOG.info("Initialization done");
   }
}
