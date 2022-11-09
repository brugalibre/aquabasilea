package com.aquabasilea.persistence.entity.userconfig.dao;

import com.aquabasilea.persistence.entity.userconfig.UserConfigEntity;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserConfigDao extends CrudRepository<UserConfigEntity, String> {
   /**
    * Returns an entity which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    * @return an entity from type {@link UserConfigEntity} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link UserConfigEntity} associated with the given user-id
    */
   UserConfigEntity getByUserId(String userId);
}
