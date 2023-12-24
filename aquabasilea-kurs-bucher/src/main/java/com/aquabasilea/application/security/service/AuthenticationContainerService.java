package com.aquabasilea.application.security.service;

import com.aquabasilea.application.security.model.AuthenticationContainer;
import com.aquabasilea.application.security.service.securestorage.SecretStoreService;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationContainerService {

   private final UserRepository userRepository;
   private final SecretStoreService secretStoreService;

   public AuthenticationContainerService(UserRepository userRepository, SecretStoreService secretStoreService) {
      this.secretStoreService = secretStoreService;
      this.userRepository = userRepository;
   }

   public AuthenticationContainer getAuthenticationContainer(String userId) {
      User user = userRepository.getById(userId);
      return new AuthenticationContainer(user.username(), () -> secretStoreService.getUserPassword(user.username()));
   }
}
