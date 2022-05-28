package com.aquabasilea.persistence.config;

import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.impl.CourseDefRepositoryImpl;
import com.aquabasilea.course.user.repository.WeeklyCoursesRepository;
import com.aquabasilea.course.user.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.persistence.entity.course.aquabasilea.dao.CoursesDefDao;
import com.aquabasilea.persistence.entity.course.user.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.entity.statistic.dao.StatisticsDao;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import com.aquabasilea.statistics.repository.impl.StatisticsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = {WeeklyCoursesDao.class, CoursesDefDao.class, StatisticsDao.class})
@EntityScan(basePackages = {"com.aquabasilea.persistence.entity"})
@ComponentScan(basePackages = {"com.aquabasilea"})
public class AquabasileaCourseBookerPersistenceConfig {

   /**
    * Name of the {@link WeeklyCoursesRepository}-Bean
    */
   public static final String WEEKLY_COURSES_REPOSITORY_BEAN = "weeklyCoursesRepository";
   public static final String COURSE_DEF_REPOSITORY_BEAN = "courseDefRepository";
   public static final String STATISTICS_REPOSITORY_BEAN = "statisticsRepository";

   @Bean(name = WEEKLY_COURSES_REPOSITORY_BEAN)
   public WeeklyCoursesRepository getWeeklyCoursesRepository(@Autowired WeeklyCoursesDao weeklyCoursesDao) {
      return new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
   }

   @Bean(name = COURSE_DEF_REPOSITORY_BEAN)
   public CourseDefRepository getCourseDefRepository(@Autowired CoursesDefDao coursesDefDao) {
      return new CourseDefRepositoryImpl(coursesDefDao);
   }

   @Bean(name = STATISTICS_REPOSITORY_BEAN)
   public StatisticsRepository getStatisticsRepository(@Autowired StatisticsDao statisticsDao) {
      return new StatisticsRepositoryImpl(statisticsDao);
   }
}
