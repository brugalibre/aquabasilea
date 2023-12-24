package com.aquabasilea.application.config;

import com.aquabasilea.persistence.coursedef.dao.CoursesDefDao;
import com.aquabasilea.persistence.courselocation.dao.CourseLocationDao;
import com.aquabasilea.persistence.courses.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.statistics.dao.StatisticsDao;
import com.aquabasilea.persistence.userconfig.dao.UserConfigDao;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {WeeklyCoursesDao.class, CoursesDefDao.class, StatisticsDao.class,
        UserConfigDao.class, CourseLocationDao.class})
@EntityScan(basePackages = {"com.aquabasilea.persistence"})
@ComponentScan(basePackages = {"com.aquabasilea.application.security.service", "com.aquabasilea.application.initialize"})
public class AquabasileaCourseBookerPersistenceConfig {
   // no-op
}
