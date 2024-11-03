package com.aquabasilea.application.config;

import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.healthcheck.model.repository.HealthCheckRepository;
import com.aquabasilea.domain.healthcheck.service.HealthCheckCourseEvaluator;
import com.aquabasilea.domain.healthcheck.service.HealthCheckService;
import com.aquabasilea.domain.healthcheck.service.HealthCheckUserIdEvaluator;
import com.aquabasilea.persistence.healthcheck.dao.HealthCheckDao;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.brugalibre.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheckConfig {

   public static final String HEALTHCHECK_TECH_USER_NAME = "${application.healthcheck.tech-user-name:}";
   public static final String HEALTH_CHECK_REPOSITORY_BEAN = "healthCheckRepository";
   public static final String HEALTH_CHECK_SERVICE = "healthCheckService";
   public static final String HEALTH_CHECK_COURSE_EVALUATOR = "healthCheckCourseEvaluator";
   public static final String USER_ID_EVALUATOR = "UserIdEvaluator";

   @Bean(name = HEALTH_CHECK_REPOSITORY_BEAN)
   public HealthCheckRepository getHealthCheckRepository(@Autowired HealthCheckDao healthCheckDao) {
      return new HealthCheckRepository(healthCheckDao);
   }

   @Bean(name = HEALTH_CHECK_SERVICE)
   public HealthCheckService getHealthCheckService(@Autowired HealthCheckUserIdEvaluator healthCheckUserIdEvaluator,
                                                   @Autowired HealthCheckRepository healthCheckRepository,
                                                   @Autowired AquabasileaCourseBookerService aquabasileaCourseBookerService,
                                                   @Autowired HealthCheckCourseEvaluator healthCheckCourseEvaluator) {
      return new HealthCheckService(healthCheckUserIdEvaluator, aquabasileaCourseBookerService, healthCheckCourseEvaluator, healthCheckRepository);
   }

   @Bean(name = HEALTH_CHECK_COURSE_EVALUATOR)
   public HealthCheckCourseEvaluator getHealthCheckCourseEvaluator(@Autowired HealthCheckUserIdEvaluator healthCheckUserIdEvaluator,
                                                                   @Autowired WeeklyCoursesRepository weeklyCoursesRepository) {
      return new HealthCheckCourseEvaluator(healthCheckUserIdEvaluator, weeklyCoursesRepository);
   }

   @Bean(name = USER_ID_EVALUATOR)
   public HealthCheckUserIdEvaluator getHealthCheckUserIdEvaluator(@Value(HEALTHCHECK_TECH_USER_NAME) String userName,
                                                                   @Autowired UserRepository userRepository) {
      return new HealthCheckUserIdEvaluator(userName, userRepository);
   }
}

