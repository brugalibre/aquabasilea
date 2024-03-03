package com.aquabasilea.domain.healthcheck.service;

import com.brugalibre.domain.user.repository.UserRepository;

import java.util.function.Supplier;

public class HealthCheckUserIdEvaluator implements Supplier<String> {
   private final String userName;
   private final UserRepository userRepository;

   public HealthCheckUserIdEvaluator(String userName, UserRepository userRepository) {
      this.userName = userName;
      this.userRepository = userRepository;
   }

   /**
    * @return the id of a user with the given name
    */
   @Override
   public String get() {
      return userRepository.findByUsername(userName)
              .orElseThrow()
              .getId();
   }
}
