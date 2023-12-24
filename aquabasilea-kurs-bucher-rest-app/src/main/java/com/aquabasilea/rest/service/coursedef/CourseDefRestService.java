package com.aquabasilea.rest.service.coursedef;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.rest.service.coursedef.mapper.CourseLocationDtoMapper;
import com.aquabasilea.service.coursedef.CourseDefService;
import com.aquabasilea.service.courselocation.CourseLocationCache;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CourseDefRestService {

   private final CourseDefService courseDefService;
   private final CourseLocationDtoMapper courseLocationDtoMapper;
   private final CourseLocationCache courseLocationCache;

   @Autowired
   public CourseDefRestService(CourseDefService courseDefService, CourseLocationCache courseLocationCache,
                               CourseLocationDtoMapper courseLocationDtoMapper) {
      this.courseLocationCache = courseLocationCache;
      this.courseLocationDtoMapper = courseLocationDtoMapper;
      this.courseDefService = courseDefService;
   }

   public boolean isCourseDefUpdateRunning(String currentUserId) {
      return courseDefService.isCourseDefUpdateRunning(currentUserId);
   }

   public void updateCourseDefs(String userId, List<String> courseLocationDtos) {
      List<CourseLocation> courseLocations = courseLocationDtoMapper.mapToCourseLocation(courseLocationDtos);
      courseDefService.updateAquabasileaCourses(userId, courseLocations);
   }

   public List<CourseDefDto> getCourseDefDtos(String currentUserId) {
      return getAllCourseDefDtos(currentUserId).stream()
              .sorted(Comparator.comparing(CourseDefDto::courseName))
              .toList();
   }

   private List<CourseDefDto> getAllCourseDefDtos(String currentUserId) {
      return courseDefService.getAllByUserId(currentUserId)
              .stream()
              .map(CourseDefDto::of)
              .toList();
   }

   /**
    * Returns a list with {@link CourseLocationDto}s for all existing {@link CourseLocation}s. All {@link CourseLocationDto}
    * which are actually stored for the user are marked as 'selected'
    *
    * @param userId the technical id of a {@link User}
    * @return a {@link List} with {@link CourseLocationDto}s for all existing {@link CourseLocation}s
    */
   public List<CourseLocationDto> getCourseLocationsDtosByUserId(String userId) {
      List<CourseLocation> courseLocations = courseDefService.getCourseLocationsByUserId(userId);
      return courseLocationCache.getAll()
              .stream()
              .map(courseLocation -> courseLocationDtoMapper.mapToCourseLocationDto(courseLocation, courseLocations.contains(courseLocation)))
              .toList();
   }
}
