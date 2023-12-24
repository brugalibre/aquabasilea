package com.aquabasilea.rest.service.coursedef.mapper;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.service.courselocation.CourseLocationCache;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseLocationDtoMapper {
   private final CourseLocationCache courseLocationCache;

   public CourseLocationDtoMapper(CourseLocationCache courseLocationCache) {
      this.courseLocationCache = courseLocationCache;
   }

   /**
    * Maps the given {@link CourseLocationDto} to {@link CourseLocation}s
    *
    * @param courseLocationCenterIds the ids of the {@link CourseLocation} to map
    * @return a list of {@link CourseLocation}s
    */
   public List<CourseLocation> mapToCourseLocation(List<String> courseLocationCenterIds) {
      return courseLocationCenterIds.stream()
              .map(courseLocationCache::getByCenterId)
              .toList();
   }

   /**
    * Maps the given {@link CourseLocation} to a {@link CourseLocationDto}
    *
    * @param courseLocation the {@link CourseLocation} to map
    * @param isSelected     <code>true</code> if the given {@link CourseLocation} is selected for a specific user
    * @return a mapped {@link CourseLocationDto}
    */
   public CourseLocationDto mapToCourseLocationDto(CourseLocation courseLocation, boolean isSelected) {
      return CourseLocationDto.of(courseLocation, isSelected);
   }
}
