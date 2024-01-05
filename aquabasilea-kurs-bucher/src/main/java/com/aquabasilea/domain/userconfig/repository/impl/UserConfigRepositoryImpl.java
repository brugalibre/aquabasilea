package com.aquabasilea.domain.userconfig.repository.impl;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.userconfig.model.DefaultUserConfig;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.model.mapping.UserConfigEntityMapperImpl;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.userconfig.UserConfigEntity;
import com.aquabasilea.persistence.userconfig.dao.UserConfigDao;
import com.aquabasilea.service.courselocation.CourseLocationCache;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

import java.util.List;

public class UserConfigRepositoryImpl extends CommonDomainRepositoryImpl<UserConfig, UserConfigEntity, UserConfigDao> implements UserConfigRepository {

   private final CourseLocationCache courseLocationCache;

   public UserConfigRepositoryImpl(UserConfigDao userConfigDao, CourseLocationCache courseLocationCache) {
      super(userConfigDao, new UserConfigEntityMapperImpl());
      this.courseLocationCache = courseLocationCache;
   }

   @Override
   public UserConfig getByUserId(String userId) {
      return domainModelMapper.map2DomainModel(domainDao.getByUserId(userId));
   }

   @Override
   public void deleteByUserId(String userId) {
      domainDao.delete(domainDao.getByUserId(userId));
   }

   @Override
   public List<CourseLocation> getDefaultCourseLocations() {
      return courseLocationCache.getAll()
              .stream().filter(courseLocation -> DefaultUserConfig.DEFAULT_COURSE_LOCATION_CENTER_IDS.contains(courseLocation.centerId()))
              .toList();
   }
}
