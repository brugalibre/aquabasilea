package com.aquabasilea.rest.api;

import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import com.aquabasilea.rest.service.WeeklyCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/v1/aquabasilea-course-booker/weekly-courses")
@RestController
public class WeeklyCoursesController {

   private final WeeklyCoursesService weeklyCoursesService;

   @Autowired
   public WeeklyCoursesController(WeeklyCoursesService weeklyCoursesService) {
      this.weeklyCoursesService = weeklyCoursesService;
   }

   @GetMapping(path = "/getWeeklyCourses")
   public WeeklyCoursesDto getWeeklyCourses() {
      return weeklyCoursesService.getWeeklyCoursesDto();
   }

   @PostMapping(path = "/addCourse")
   public int addCourse(@Valid @NonNull @RequestBody CourseDto courseDto) {
      weeklyCoursesService.addCourse(courseDto);
      return HttpStatus.OK.value();
   }

   @PostMapping(path = "/pauseResumeCourse/{courseId}")
   public int pauseResumeCourse(@PathVariable String courseId) {
      weeklyCoursesService.pauseResumeCourse(courseId);
      return HttpStatus.OK.value();
   }

   @DeleteMapping(path="{courseId}")
   public int deleteCourse(@PathVariable String courseId) {
      weeklyCoursesService.deleteCourseById(courseId);
      return HttpStatus.OK.value();
   }
}
