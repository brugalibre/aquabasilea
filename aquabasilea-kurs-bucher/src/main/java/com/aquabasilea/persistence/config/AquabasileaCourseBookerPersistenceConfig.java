package com.aquabasilea.persistence.config;

import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.course.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.persistence.entity.course.dao.WeeklyCoursesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = {WeeklyCoursesDao.class})
@EntityScan(basePackages = {"com.aquabasilea.persistence.entity"})
@ComponentScan(basePackages = {"com.aquabasilea"})
public class AquabasileaCourseBookerPersistenceConfig {

   /**
    * Name of the {@link WeeklyCoursesRepository}-Bean
    */
   public static final String WEEKLY_COURSES_REPOSITORY_BEAN = "weeklyCoursesRepository";

   @Bean(name = WEEKLY_COURSES_REPOSITORY_BEAN)
   public WeeklyCoursesRepository getWeeklyCoursesRepository(@Autowired WeeklyCoursesDao weeklyCoursesDao) {
      return new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
   }
}
