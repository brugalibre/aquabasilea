package com.aquabasilea.application.initialize;

import com.aquabasilea.application.config.logging.MdcConst;
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
import org.slf4j.MDC;
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
    * This initialization e.g. happens when a new {@link User} was registered.
    * - First initialize the credentials of the user
    * - then initialize the persistence
    * - initialize the AquabasileaCourseBooker itself
    * - and last but not least initialize the course-definitions
    *
    * @param userAddedEvent the {@link UserAddedEvent} with details about the added user
    */
   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      MDC.put(MdcConst.USER_ID, userAddedEvent.userId());
      userCredentialsHandler.initialize(userAddedEvent);
      persistenceInitializer.initialize(userAddedEvent);
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
      // The Scheduler has a dependency on the aquabasileaCourseBooker, so we need that first
      courseDefUpdater.startScheduler(userAddedEvent.userId());
      MDC.remove(MdcConst.USER_ID);
   }

   @Override
   public void initialize4ExistingUsers() {
      List<User> registeredUsers = userRepository.getAll();
      LOG.info("Going to initialize for total {} users..", registeredUsers.size());
      for (User user : registeredUsers) {
         MDC.put(MdcConst.USER_ID, user.getId());
         UserAddedEvent userAddedEvent = UserAddedEvent.of(user);
         aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
         // The Scheduler has a dependency on the aquabasileaCourseBooker, so we need that first
         // TODO: refactor with some kind of initializer interface which has a order and type (e.g. initialize for register and start-up)
         courseDefUpdater.startScheduler(userAddedEvent.userId());
         userRoleConfigService.addMissingRoles(user.id());
      }
      MDC.remove(MdcConst.USER_ID);
      LOG.info("Initialization done");
   }
}
