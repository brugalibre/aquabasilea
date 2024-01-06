package com.aquabasilea.domain.coursedef.update.service;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.service.userconfig.UserConfigService;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.brugalibre.domain.user.model.User;

import java.util.List;

/**
 * The {@link CourseDefUpdaterService} is responsible for updating the {@link CourseDef} for a certain user
 * but also for updating the {@link Statistics} as well as the {@link UserConfig} if the default courses has changed
 */
public class CourseDefUpdaterService {

   private final CourseDefUpdater courseDefUpdater;
   private final UserConfigService userConfigService;

   public CourseDefUpdaterService(CourseDefUpdater courseDefUpdater, UserConfigService userConfigService) {
      this.courseDefUpdater = courseDefUpdater;
      this.userConfigService = userConfigService;
   }

   /**
    * Updates all {@link CourseDef} according user's configuration (which defines the {@link CourseLocation}s to consider)
    * According to those the Aquabasilea-courses defined on their course-page are considered
    *
    * @param userId          the id of the {@link User}
    * @param courseLocations the {@link CourseLocation}s which are considered when updating the {@link CourseDef}s
    */
   public void updateAquabasileaCourses(String userId, List<CourseLocation> courseLocations) {
      userConfigService.updateCourseLocations(userId, courseLocations);
      courseDefUpdater.updateAquabasileaCourses(userId);
   }

   /**
    * @param userId the id of the {@link User}
    * @return <code>true</code> if there is currently an update running for the given user id. Returns <code>false</code> otherwise
    */
   public boolean isCourseDefUpdateRunning(String userId) {
      return courseDefUpdater.isCourseDefUpdateRunning(userId);
   }

   /**
    * Adds the given {@link CourseDefUpdatedNotifier} to the list. All {@link CourseDefUpdatedNotifier} are notified as soon
    * as a {@link CourseDef} update is done
    *
    * @param courseDefUpdatedNotifier the {@link CourseDefUpdatedNotifier} to add
    */
   public void addCourseDefUpdatedNotifier(CourseDefUpdatedNotifier courseDefUpdatedNotifier) {
      courseDefUpdater.addCourseDefUpdatedNotifier(courseDefUpdatedNotifier);
   }
}
