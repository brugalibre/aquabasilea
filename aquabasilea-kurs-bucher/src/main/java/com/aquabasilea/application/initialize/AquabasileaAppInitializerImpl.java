package com.aquabasilea.application.initialize;

import com.aquabasilea.application.config.logging.MdcConst;
import com.aquabasilea.application.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.application.initialize.api.Initializer;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aquabasilea.application.initialize.common.InitializationConst.compareOrder;
import static com.aquabasilea.application.initialize.common.InitializationConst.isInitializerForType;

@Service
public class AquabasileaAppInitializerImpl implements AquabasileaAppInitializer {

   private final static Logger LOG = LoggerFactory.getLogger(AquabasileaAppInitializerImpl.class);
   private final UserRepository userRepository;
   private final List<Initializer> initializers;

   @Autowired
   public AquabasileaAppInitializerImpl(UserRepository userRepository,
                                        List<Initializer> initializers) {
      this.initializers = initializers;
      this.userRepository = userRepository;
   }

   /**
    * Initializes the entire Aquabasilea-application including the persistence as well as the {@link AquabasileaCourseBooker}
    * This initialization e.g. happens when a new {@link User} was registered. The order in which the {@link Initializer}s
    * are executed is defined by ther {@link InitializeOrder} annotation
    *
    * @param userAddedEvent the {@link UserAddedEvent} with details about the added user
    */
   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      MDC.put(MdcConst.USER_ID, userAddedEvent.userId());
      executeInitializers(InitType.USER_ADDED, userAddedEvent);
      MDC.remove(MdcConst.USER_ID);
   }

   /**
    * Initializes the entire Aquabasilea-application. This initialization e.g. happens when the application server is started
    * and all elements of the aquabasilea-course booker applications has to be created/initialized for all existing users.
    * <b>Note:</b> We assume here that e.g. the persistence is already initialized
    * The order in which the {@link Initializer}s are executed is defined by ther {@link InitializeOrder} annotation
    *
    */
   @Override
   public void initializeOnAppStart() {
      List<User> registeredUsers = userRepository.getAll();
      LOG.info("Going to initialize for total {} users..", registeredUsers.size());
      for (User user : registeredUsers) {
         MDC.put(MdcConst.USER_ID, user.getId());
         executeInitializers(InitType.USER_ACTIVATED, UserAddedEvent.of(user));
      }
      MDC.remove(MdcConst.USER_ID);
      LOG.info("Initialization done");
   }

   private void executeInitializers(InitType initType, UserAddedEvent userAddedEvent) {
      initializers.stream()
              .filter(isInitializerForType(initType))
              .sorted(compareOrder())
              .forEach(initializer -> initializer.initialize(userAddedEvent));
   }
}
