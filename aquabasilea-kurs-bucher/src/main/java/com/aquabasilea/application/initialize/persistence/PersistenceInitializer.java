package com.aquabasilea.application.initialize.persistence;

import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;

import static com.aquabasilea.application.initialize.common.InitializationConst.AQUABASILEA_PERSISTENCE;

@Service
@InitializeOrder(order = AQUABASILEA_PERSISTENCE, type = {InitType.USER_ADDED})
public class PersistenceInitializer implements InitializerForUser {
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final StatisticsRepository statisticsRepository;
   private final UserConfigRepository userConfigRepository;

   @Autowired
   public PersistenceInitializer(WeeklyCoursesRepository weeklyCoursesRepository, StatisticsRepository statisticsRepository,
                                 UserConfigRepository userConfigRepository) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.statisticsRepository = statisticsRepository;
      this.userConfigRepository = userConfigRepository;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      weeklyCoursesRepository.save(new WeeklyCourses(userAddedEvent.userId()));
      statisticsRepository.save(new Statistics(userAddedEvent.userId(), Clock.systemDefaultZone()));
      userConfigRepository.save(new UserConfig(userAddedEvent.userId(), userConfigRepository.getDefaultCourseLocations()));
   }
}
