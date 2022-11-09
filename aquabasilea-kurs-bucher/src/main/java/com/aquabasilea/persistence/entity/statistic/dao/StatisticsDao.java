package com.aquabasilea.persistence.entity.statistic.dao;

import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.data.repository.CrudRepository;

public interface StatisticsDao extends CrudRepository<StatisticsEntity, String> {
   /**
    * Returns an entity which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    * @return an entity from type {@link StatisticsEntity} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link StatisticsEntity} associated with the given user-id
    */
   StatisticsEntity getByUserId(String userId);
}
