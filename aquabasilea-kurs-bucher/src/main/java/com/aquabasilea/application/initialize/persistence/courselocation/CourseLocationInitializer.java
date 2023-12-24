package com.aquabasilea.application.initialize.persistence.courselocation;

import com.aquabasilea.application.initialize.api.AppInitializer;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseLocationExtractorFacade;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aquabasilea.application.initialize.common.InitializationConst.COURSE_LOCATIONS;

@Service
@InitializeOrder(order = COURSE_LOCATIONS, type = {InitType.APP_STARTED})
public class CourseLocationInitializer implements AppInitializer {
   private final CourseLocationRepository courseLocationRepository;
   private final CourseLocationExtractorFacade courseLocationExtractorFacade;
   private static final Logger LOG = LoggerFactory.getLogger(CourseLocationInitializer.class);

   @Autowired
   public CourseLocationInitializer(CourseLocationRepository courseLocationRepository,
                                    CourseBookerFacadeFactory courseBookerFacadeFactory) {
      this.courseLocationRepository = courseLocationRepository;
      this.courseLocationExtractorFacade = courseBookerFacadeFactory.createCourseLocationExtractorFacade();
   }

   @Override
   public void initializeOnAppStart() {
      if (courseLocationRepository.getAll().isEmpty()) {
         List<CourseLocation> courseLocations = courseLocationExtractorFacade.getCourseLocations();
         courseLocationRepository.saveAll(courseLocations);
         LOG.info("Evaluated {} course-locations", courseLocations.size());
      } else {
         LOG.info("Courselocations are already persisted, nothing to done!");
      }
   }
}
