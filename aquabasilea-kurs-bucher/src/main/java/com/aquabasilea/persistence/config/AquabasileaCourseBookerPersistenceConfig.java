package com.aquabasilea.persistence.config;

import com.aquabasilea.persistence.entity.course.aquabasilea.dao.CoursesDefDao;
import com.aquabasilea.persistence.entity.course.weeklycourses.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.entity.statistic.dao.StatisticsDao;
import com.aquabasilea.persistence.entity.userconfig.dao.UserConfigDao;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {WeeklyCoursesDao.class, CoursesDefDao.class, StatisticsDao.class, UserConfigDao.class})
@EntityScan(basePackages = {"com.aquabasilea.persistence.entity"})
@ComponentScan(basePackages = {"com.aquabasilea.persistence.entity"})
public class AquabasileaCourseBookerPersistenceConfig {
   // no-op
}
