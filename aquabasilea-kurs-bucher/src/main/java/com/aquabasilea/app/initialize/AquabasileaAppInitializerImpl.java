package com.aquabasilea.app.initialize;

import com.aquabasilea.app.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.app.initialize.coursebooker.AquabasileaCourseBookerInitializer;
import com.aquabasilea.app.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.app.initialize.usercredentials.UserCredentialsInitializer;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.model.course.coursedef.update.CourseDefUpdater;
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
   private final UserCredentialsInitializer userCredentialsInitializer;
   private final PersistenceInitializer persistenceInitializer;
   private final AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;
   private final CourseDefUpdater courseDefUpdater;
   private final UserRepository userRepository;
   private final UserRoleConfigService userRoleConfigService;

   @Autowired
   public AquabasileaAppInitializerImpl(PersistenceInitializer persistenceInitializer,
                                        UserCredentialsInitializer userCredentialsInitializer,
                                        AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer,
                                        CourseDefUpdater courseDefUpdater,
                                        UserRepository userRepository,
                                        UserRoleConfigService userRoleConfigService) {
      this.persistenceInitializer = persistenceInitializer;
      this.userCredentialsInitializer = userCredentialsInitializer;
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
      userCredentialsInitializer.initialize(userAddedEvent);
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
