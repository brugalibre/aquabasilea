package com.aquabasilea.rest.api.course;

import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import com.aquabasilea.rest.service.course.WeeklyCoursesRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/aquabasilea-course-booker/weekly-courses")
@RestController
public class WeeklyCoursesController {

   private final WeeklyCoursesRestService weeklyCoursesRestService;
   private final IUserProvider userProvider;

   @Autowired
   public WeeklyCoursesController(WeeklyCoursesRestService weeklyCoursesRestService, IUserProvider userProvider) {
      this.weeklyCoursesRestService = weeklyCoursesRestService;
      this.userProvider = userProvider;
   }

   @GetMapping(path = "/weeklyCourses")
   public WeeklyCoursesDto getWeeklyCourses() {
      return weeklyCoursesRestService.getWeeklyCoursesDto(userProvider.getCurrentUserId());
   }

   @RequestMapping(value = "/course", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
   public int addCourse(@NonNull @RequestBody CourseDto courseDto) {
      weeklyCoursesRestService.addCourse(courseDto, userProvider.getCurrentUserId());
      return HttpStatus.OK.value();
   }

   @PutMapping(path = "/pauseResumeCourse/{courseId}")
   public int pauseResumeCourse(@PathVariable String courseId) {
      weeklyCoursesRestService.pauseResumeCourse(courseId, userProvider.getCurrentUserId());
      return HttpStatus.OK.value();
   }

   @DeleteMapping(path = "{courseId}")
   public int deleteCourse(@PathVariable String courseId) {
      weeklyCoursesRestService.deleteCourseById(courseId, userProvider.getCurrentUserId());
      return HttpStatus.OK.value();
   }
}
