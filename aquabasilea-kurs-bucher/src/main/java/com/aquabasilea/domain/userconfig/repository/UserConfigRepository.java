package com.aquabasilea.domain.userconfig.repository;

import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

/**
 * The {@link UserConfigRepository} is responsible for loading and saving a {@link UserConfig}
 */
public interface UserConfigRepository extends UserRelatedRepository<UserConfig> {
   // no-op
}
