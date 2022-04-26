package com.aquabasilea.rest.api;

import com.aquabasilea.rest.model.course.CourseDto;
import com.aquabasilea.rest.model.course.WeeklyCoursesDto;
import com.aquabasilea.rest.service.WeeklyCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

   @GetMapping(path = "/getDaysOfTheWeek4Course/{courseName}")
   public List<String> getDaysOfTheWeek4Course(@NonNull @PathVariable String courseName) {
      return weeklyCoursesService.getDaysOfTheWeek4Course(courseName);
   }

   @PostMapping(path = "/addCourse")
   public int addCourse(@Valid @NonNull @RequestBody CourseDto courseDto) {
      weeklyCoursesService.addCourse(courseDto);
      return HttpStatus.OK.value();
   }

   @PostMapping(path = "/changeCourse")
   public int changeCourse(@Valid @NonNull @RequestBody CourseDto courseDto) {
      weeklyCoursesService.changeCourse(courseDto);
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
