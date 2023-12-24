package com.aquabasilea.rest.service.course;

import com.aquabasilea.domain.course.model.WeeklyCoursesOverview;
import com.aquabasilea.rest.model.course.mapper.CourseDtoMapper;
import com.aquabasilea.rest.model.course.mapper.WeeklyCoursesDtoMapper;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import com.aquabasilea.service.courses.WeeklyCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyCoursesRestService {

   private final WeeklyCoursesDtoMapper weeklyCoursesDtoMapper;
   private final WeeklyCoursesService weeklyCoursesService;
   private final CourseDtoMapper courseDtoMapper;

   @Autowired
   public WeeklyCoursesRestService(WeeklyCoursesService weeklyCoursesService, CourseDtoMapper courseDtoMapper,
                                   WeeklyCoursesDtoMapper weeklyCoursesDtoMapper) {
      this.weeklyCoursesService = weeklyCoursesService;
      this.weeklyCoursesDtoMapper = weeklyCoursesDtoMapper;
      this.courseDtoMapper = courseDtoMapper;
   }

   public void addCourse(CourseDto courseDto, String userId) {
      weeklyCoursesService.addCourse(courseDtoMapper.map2Course(courseDto), userId);
   }

   public void pauseResumeCourse(String courseId, String userId) {
      weeklyCoursesService.pauseResumeCourse(courseId, userId);
   }

   public void deleteCourseById(String courseId2Delete, String userId) {
      weeklyCoursesService.deleteCourseById(courseId2Delete, userId);
   }

   public WeeklyCoursesDto getWeeklyCoursesDto(String userId) {
      WeeklyCoursesOverview weeklyCoursesOverview = weeklyCoursesService.getWeeklyCoursesOverviewByUserId(userId);
      return weeklyCoursesDtoMapper.mapToWeeklyCourseDto(weeklyCoursesOverview.weeklyCourses(), weeklyCoursesOverview.currentCourse());
   }

}
