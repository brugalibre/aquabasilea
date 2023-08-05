package com.aquabasilea.service.userconfig;

import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConfigService {
   private final UserConfigRepository userConfigRepository;

   @Autowired
   public UserConfigService(UserConfigRepository userConfigRepository) {
      this.userConfigRepository = userConfigRepository;
   }

   /**
    * Returns the {@link UserConfig} instance associated with an {@link User} with the given id
    *
    * @param userId the technical id of the {@link User}
    * @return the {@link UserConfig} instance associated with an {@link User} with the given id
    */
   public UserConfig getByUserId(String userId) {
      return userConfigRepository.getByUserId(userId);
   }

   /**
    * Returns all {@link CourseLocation}s which the {@link User} with the given id has stored
    *
    * @param userId the technical id of the {@link User}
    * @return all {@link CourseLocation}s which the {@link User} with the given id has stored
    */
   public List<CourseLocation> getCourseLocations4UserId(String userId) {
      return getByUserId(userId).getCourseLocations();
   }

   /**
    * Updates the {@link CourseLocation} on the {@link UserConfig} for the given user id
    *
    * @param userId          the id of the {@link User} for whom the {@link CourseLocation} should be updated
    * @param courseLocations the new {@link CourseLocation}s
    * @return an updated instance of the {@link UserConfig}
    */
   public UserConfig updateCourseLocations(String userId, List<CourseLocation> courseLocations) {
      UserConfig userConfig = userConfigRepository.getByUserId(userId);
      userConfig.setCourseLocations(courseLocations);
      return userConfigRepository.save(userConfig);
   }
}
