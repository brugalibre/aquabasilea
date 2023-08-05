package com.aquabasilea.rest.service.course;

import com.aquabasilea.domain.course.WeeklyCoursesOverview;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import com.aquabasilea.service.courses.WeeklyCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyCoursesRestService {

   private final WeeklyCoursesService weeklyCoursesService;
   private final LocaleProvider localeProvider;

   @Autowired
   public WeeklyCoursesRestService(WeeklyCoursesService weeklyCoursesService, LocaleProvider localeProvider) {
      this.weeklyCoursesService = weeklyCoursesService;
      this.localeProvider = localeProvider;
   }

   public void addCourse(CourseDto courseDto, String userId) {
      weeklyCoursesService.addCourse(CourseDto.map2Course(courseDto), userId);
   }

   public void pauseResumeCourse(String courseId, String userId) {
      weeklyCoursesService.pauseResumeCourse(courseId, userId);
   }

   public void deleteCourseById(String courseId2Delete, String userId) {
      weeklyCoursesService.deleteCourseById(courseId2Delete, userId);
   }

   public WeeklyCoursesDto getWeeklyCoursesDto(String userId) {
      WeeklyCoursesOverview weeklyCoursesOverview = weeklyCoursesService.getWeeklyCoursesOverviewByUserId(userId);
      return WeeklyCoursesDto.of(weeklyCoursesOverview.weeklyCourses(), weeklyCoursesOverview.currentCourse(), localeProvider.getCurrentLocale());
   }

}
