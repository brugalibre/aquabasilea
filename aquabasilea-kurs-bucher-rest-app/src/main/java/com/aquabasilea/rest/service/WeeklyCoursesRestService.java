package com.aquabasilea.rest.service;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.service.weeklycourses.WeeklyCoursesService;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyCoursesRestService {

   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesRestService.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final WeeklyCoursesService weeklyCoursesService;

   @Autowired
   public WeeklyCoursesRestService(WeeklyCoursesService weeklyCoursesService, AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.weeklyCoursesService = weeklyCoursesService;
   }

   public void addCourse(CourseDto courseDto, String userId) {
      LOG.info("Add course {}", courseDto);
      WeeklyCourses weeklyCourses = getWeeklyCourses4CurrentUser(userId);
      weeklyCourses.addCourse(CourseDto.map2Course(courseDto));
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   public void pauseResumeCourse(String courseId, String userId) {
      LOG.info("Pausing / resuming course {}", courseId);
      WeeklyCourses weeklyCourses = getWeeklyCourses4CurrentUser(userId);
      weeklyCourses.pauseResumeCourse(courseId);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   public void deleteCourseById(String courseId2Delete, String userId) {
      LOG.info("Delete course with id {}", courseId2Delete);
      WeeklyCourses weeklyCourses = getWeeklyCourses4CurrentUser(userId);
      weeklyCourses.removeCourseById(courseId2Delete);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   public WeeklyCoursesDto getWeeklyCoursesDto(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return WeeklyCoursesDto.of(getWeeklyCourses4CurrentUser(userId),
              aquabasileaCourseBooker.getCurrentCourse(), LocaleProvider.getCurrentLocale());
   }

   private void changeWeeklyCourseAndRefreshCourseBooker(WeeklyCourses weeklyCourses, String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      weeklyCoursesService.save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();
   }

   private WeeklyCourses getWeeklyCourses4CurrentUser(String userId) {
      return weeklyCoursesService.getByUserId(userId);
   }

   private AquabasileaCourseBooker getAquabasileaCourseBooker4CurrentUser(String userId) {
      return aquabasileaCourseBookerHolder.getForUserId(userId);
   }
}
