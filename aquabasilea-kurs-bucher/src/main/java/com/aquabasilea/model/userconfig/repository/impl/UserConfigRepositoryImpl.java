package com.aquabasilea.model.userconfig.repository.impl;

import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.model.userconfig.repository.mapping.UserConfigEntityMapperImpl;
import com.aquabasilea.persistence.entity.userconfig.UserConfigEntity;
import com.aquabasilea.persistence.entity.userconfig.dao.UserConfigDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

public class UserConfigRepositoryImpl extends CommonDomainRepositoryImpl<UserConfig, UserConfigEntity, UserConfigDao> implements UserConfigRepository {

   public UserConfigRepositoryImpl(UserConfigDao userConfigDao) {
      super(userConfigDao, new UserConfigEntityMapperImpl());
   }

   @Override
   public UserConfig getByUserId(String userId) {
      return domainModelMapper.map2DomainModel(domainDao.getByUserId(userId));
   }
}
