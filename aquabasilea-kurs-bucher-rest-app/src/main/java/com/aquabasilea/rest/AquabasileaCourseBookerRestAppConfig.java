package com.aquabasilea.rest;

import com.aquabasilea.alerting.consumer.impl.AlertSender;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.course.aquabasilea.update.CourseDefUpdater;
import com.aquabasilea.course.user.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.persistence.config.AquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import static com.aquabasilea.persistence.config.AquabasileaCourseBookerPersistenceConfig.WEEKLY_COURSES_REPOSITORY_BEAN;

@Configuration
@EnableAutoConfiguration
@Import(AquabasileaCourseBookerPersistenceConfig.class)
public class AquabasileaCourseBookerRestAppConfig {

   public static final String AQUABASILEA_COURSE_BOOKER_BEAN = "aquabasileaCourseBooker";
   public static final String COURSE_DEF_UPDATER_BEAN = "courseDefUpdater";

   private final AquabasileaCourseBookerSupplier aquabasileaCourseBookerSupplier = new AquabasileaCourseBookerSupplier();

   @DependsOn(WEEKLY_COURSES_REPOSITORY_BEAN)
   @Bean(name = AQUABASILEA_COURSE_BOOKER_BEAN)
   public AquabasileaCourseBooker getAquabasileaCourseBooker(@Autowired WeeklyCoursesRepository weeklyCoursesRepository) {
      return createAquabasileaCourseBooker(weeklyCoursesRepository);
   }

   @Bean(name = COURSE_DEF_UPDATER_BEAN)
   public CourseDefUpdater getCourseDefUpdater(@Autowired CourseDefRepository courseDefRepository) {
      return new CourseDefUpdater(AquabasileaCourseExtractorImpl::createAndInitAquabasileaWebNavigator, courseDefRepository, new CoursesDefEntityMapperImpl());
   }

   private AquabasileaCourseBooker createAquabasileaCourseBooker(WeeklyCoursesRepository weeklyCoursesRepository) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(weeklyCoursesRepository, createAquabasileaCourseBookerThread());
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new AlertSender());
      aquabasileaCourseBookerSupplier.aquabasileaCourseBooker = aquabasileaCourseBooker;
      return aquabasileaCourseBooker;
   }

   private Thread createAquabasileaCourseBookerThread() {
      Runnable threadRunnable = () -> aquabasileaCourseBookerSupplier.aquabasileaCourseBooker.run();
      return new Thread(threadRunnable);
   }

   private static class AquabasileaCourseBookerSupplier {
      private AquabasileaCourseBooker aquabasileaCourseBooker;
   }
}

