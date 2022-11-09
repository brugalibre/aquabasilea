package com.aquabasilea.app.initialize.persistence;

import com.aquabasilea.app.initialize.Initializer;
import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.statistics.Statistics;
import com.aquabasilea.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.model.userconfig.DefaultUserConfig;
import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersistenceInitializer implements Initializer {
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final StatisticsRepository statisticsRepository;
   private final UserConfigRepository userConfigRepository;
   private final List<CourseLocation> defaultCourseLocations;

   @Autowired
   public PersistenceInitializer(WeeklyCoursesRepository weeklyCoursesRepository, StatisticsRepository statisticsRepository,
                                 UserConfigRepository userConfigRepository) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.statisticsRepository = statisticsRepository;
      this.userConfigRepository = userConfigRepository;
      this.defaultCourseLocations = DefaultUserConfig.COURSE_LOCATIONS;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      weeklyCoursesRepository.save(new WeeklyCourses(userAddedEvent.userId()));
      statisticsRepository.save(new Statistics(userAddedEvent.userId()));
      userConfigRepository.save(new UserConfig(userAddedEvent.userId(), defaultCourseLocations));
   }
}
