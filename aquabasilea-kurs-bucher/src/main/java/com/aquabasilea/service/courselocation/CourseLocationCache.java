package com.aquabasilea.service.courselocation;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Simple cache which loads the {@link CourseLocation}s once from the repository and then never updates them
 */
@Service
public class CourseLocationCache {
   private final CourseLocationRepository courseLocationRepository;
   private List<CourseLocation> courseLocations;

   public CourseLocationCache(CourseLocationRepository courseLocationRepository) {
      this.courseLocationRepository = courseLocationRepository;
      this.courseLocations = null;
   }

   /**
    * Returns the {@link CourseLocation}s which matches the given center Id.
    * Throws an {@link ObjectNotFoundException} if there is no {@link CourseLocation} for the given center-id
    *
    * @param centerId the id of the center
    * @return the {@link CourseLocation}s which matches the given center Id
    */
   public CourseLocation getByCenterId(String centerId) {
      return getAll().stream()
              .filter(courseLocation -> courseLocation.centerId().equals(centerId))
              .findFirst()
              .orElseThrow(ObjectNotFoundException::new);
   }

   /**
    * @return all existing {@link CourseLocation}s
    */
   public List<CourseLocation> getAll() {
      if (courseLocations == null) {
         courseLocations = courseLocationRepository.getAll();
      }
      return courseLocations;
   }
}
