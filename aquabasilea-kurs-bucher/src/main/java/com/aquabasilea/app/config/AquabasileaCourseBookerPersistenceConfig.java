package com.aquabasilea.app.config;

import com.aquabasilea.coursebooker.persistence.course.weeklycourses.dao.WeeklyCoursesDao;
import com.aquabasilea.coursebooker.persistence.statistic.dao.StatisticsDao;
import com.aquabasilea.coursebooker.persistence.userconfig.dao.UserConfigDao;
import com.aquabasilea.coursedef.persistence.dao.CoursesDefDao;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {WeeklyCoursesDao.class, CoursesDefDao.class, StatisticsDao.class, UserConfigDao.class})
@EntityScan(basePackages = {"com.aquabasilea.coursebooker.persistence", "com.aquabasilea.coursedef.persistence"})
@ComponentScan(basePackages = {"com.aquabasilea.coursedef", "com.aquabasilea.coursebooker",
        "com.aquabasilea.security.service", "com.aquabasilea.app.initialize"})
public class AquabasileaCourseBookerPersistenceConfig {
   // no-op
}
