package com.aquabasilea.rest.api.user.change;

import com.aquabasilea.application.initialize.api.AquabasileaAppInitializer;
import com.aquabasilea.application.initialize.usercredentials.UserCredentialsHandler;
import com.brugalibre.common.security.auth.passwordchange.UserPasswordChangedEvent;
import com.brugalibre.common.security.auth.passwordchange.UserPasswordChangedObserver;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * The {@link UserChangedObserverImpl} handles as a {@link UserPasswordChangedObserver} the event of a changed users password
 * of a {@link User} It therefore forwards the event to the {@link AquabasileaAppInitializer} which creates and initializes
 * all necessary components for the new user
 */
@Service
public class UserChangedObserverImpl implements UserPasswordChangedObserver {

    private final UserCredentialsHandler userCredentialsHandler;
    private final UserRepository userRepository;

    public UserChangedObserverImpl(UserCredentialsHandler userCredentialsHandler, UserRepository userRepository) {
        this.userCredentialsHandler = userCredentialsHandler;
        this.userRepository = userRepository;
    }

    @Override
    public void passwordChanged(UserPasswordChangedEvent userPasswordChangedEvent) {
        User user = userRepository.getById(userPasswordChangedEvent.userId());
        char[] newPassword = ((String) userPasswordChangedEvent.newPassword()).toCharArray();
        userCredentialsHandler.validateAndStoreUserCredentials(user.username(), newPassword);
    }
}
