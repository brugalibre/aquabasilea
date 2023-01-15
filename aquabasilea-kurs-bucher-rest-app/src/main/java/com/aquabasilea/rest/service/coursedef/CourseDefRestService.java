package com.aquabasilea.rest.service.coursedef;

import com.aquabasilea.coursebooker.model.course.CourseLocation;
import com.aquabasilea.coursebooker.service.userconfig.UserConfigService;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.coursedef.service.CourseDefUpdaterService;
import com.aquabasilea.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.search.ObjectTextSearch;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Objects.isNull;

@Service
public class CourseDefRestService implements CourseDefUpdatedNotifier {

   private final ObjectTextSearch objectTextSearch;
   private final UserConfigService userConfigService;
   private final CourseDefUpdaterService courseDefUpdaterService;
   private final Map<String, List<CourseDefDto>> userId2CachedCourseDefDtos;

   @Autowired
   public CourseDefRestService(CourseDefUpdaterService courseDefUpdaterService, UserConfigService userConfigService,
                               ObjectTextSearch objectTextSearch) {
      this.courseDefUpdaterService = courseDefUpdaterService;
      this.userConfigService = userConfigService;
      this.courseDefUpdaterService.addCourseDefUpdatedNotifier(this);
      this.objectTextSearch = objectTextSearch;
      this.userId2CachedCourseDefDtos = new HashMap<>();
   }

   public boolean isCourseDefUpdateRunning(String currentUserId) {
      return courseDefUpdaterService.isCourseDefUpdateRunning(currentUserId);
   }

   public void updateCourseDefs(String userId, List<CourseLocation> courseLocations) {
      courseDefUpdaterService.updateAquabasileaCourses(userId, courseLocations);
   }

   public List<CourseDefDto> getCourseDefDtos4Filter(String currentUserId, String filter) {
      List<CourseDefDto> allCourseDefDtos = getAllCourseDefDtos(currentUserId);
      if (isNull(filter)) {
         return allCourseDefDtos
                 .stream()
                 .sorted(Comparator.comparing(CourseDefDto::courseName))
                 .toList();
      }
      return objectTextSearch.getWeightedObjects4Filter(allCourseDefDtos, filter);
   }

   /**
    * Returns a list with {@link CourseLocationDto}s for all existing {@link CourseLocation}s. All {@link CourseLocationDto}
    * which are actually stored for the user are marked as 'selected'
    *
    * @param userId the technical id of a {@link User}
    * @return a {@link List} with {@link CourseLocationDto}s for all existing {@link CourseLocation}s
    */
   public List<CourseLocationDto> getCourseLocationsDtosByUserId(String userId) {
      List<CourseLocation> courseLocations = userConfigService.getCourseLocations4UserId(userId);
      return Arrays.stream(CourseLocation.values())
              .map(courseLocation -> CourseLocationDto.of(courseLocation, courseLocations.contains(courseLocation)))
              .toList();
   }

   private synchronized List<CourseDefDto> getAllCourseDefDtos(String userId) {
      if (!userId2CachedCourseDefDtos.containsKey(userId)) {
         List<CourseDefDto> cachedCourseDefDtos = getAllCourseDefDtosFromRepository(userId);
         userId2CachedCourseDefDtos.put(userId, cachedCourseDefDtos);
      }
      return userId2CachedCourseDefDtos.get(userId);
   }

   private List<CourseDefDto> getAllCourseDefDtosFromRepository(String userId) {
      return courseDefUpdaterService.getAllByUserId(userId)
              .stream()
              .map(CourseDefDto::of)
              .toList();
   }

   @Override
   public void courseDefsUpdated(String userId, List<CourseDef> updatedCourseDefs) {
      userId2CachedCourseDefDtos.put(userId, updatedCourseDefs.stream()
              .map(CourseDefDto::of)
              .toList());
   }
}
