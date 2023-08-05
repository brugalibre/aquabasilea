package com.aquabasilea.domain.userconfig.repository.impl;

import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.model.mapping.UserConfigEntityMapperImpl;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.userconfig.UserConfigEntity;
import com.aquabasilea.persistence.userconfig.dao.UserConfigDao;
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
