package com.aquabasilea.rest.service;

import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.rest.model.course.CourseDto;
import com.aquabasilea.rest.model.course.WeeklyCoursesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeeklyCoursesService {

   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesService.class);
   private final AquabasileaCourseBooker aquabasileaCourseBooker;
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   public WeeklyCoursesService(WeeklyCoursesRepository weeklyCoursesRepository, AquabasileaCourseBooker aquabasileaCourseBooker) {
      this.aquabasileaCourseBooker = aquabasileaCourseBooker;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
   }

   public void addCourse(CourseDto courseDto) {
      LOG.info("Add course {}", courseDto);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.addCourse(CourseDto.map2Course(courseDto));
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public void changeCourse(CourseDto courseDto2Change) {
      LOG.info("Change course {}", courseDto2Change);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.changeCourse(CourseDto.map2Course(courseDto2Change));
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public void pauseResumeCourse(String courseId) {
      LOG.info("Pausing / resuming course {}", courseId);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.pauseResumeCourse(courseId);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public void deleteCourseById(String courseId2Delete) {
      LOG.info("Delete course with id {}", courseId2Delete);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.removeCourseById(courseId2Delete);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public List<String> getDaysOfTheWeek4Course(String courseName) {
      // Maybe later we can filter for the given course name
      return List.of("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag");
   }

   public WeeklyCoursesDto getWeeklyCoursesDto() {
      return WeeklyCoursesDto.of(weeklyCoursesRepository.findFirstWeeklyCourses(), aquabasileaCourseBooker.getCurrentCourse());
   }

   private void changeWeeklyCourseAndRefreshCourseBooker(WeeklyCourses weeklyCourses) {
      weeklyCoursesRepository.save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();
   }
}
