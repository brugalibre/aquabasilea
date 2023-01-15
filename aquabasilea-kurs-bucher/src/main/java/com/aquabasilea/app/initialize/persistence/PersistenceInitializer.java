package com.aquabasilea.app.initialize.persistence;

import com.aquabasilea.app.initialize.Initializer;
import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.coursebooker.model.course.CourseLocation;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.aquabasilea.coursebooker.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.coursebooker.model.userconfig.DefaultUserConfig;
import com.aquabasilea.coursebooker.model.userconfig.UserConfig;
import com.aquabasilea.coursebooker.model.userconfig.repository.UserConfigRepository;
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
