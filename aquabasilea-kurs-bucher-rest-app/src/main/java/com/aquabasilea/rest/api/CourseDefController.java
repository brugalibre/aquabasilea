package com.aquabasilea.rest.api;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.rest.service.coursedef.CourseDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/aquabasilea-course-booker/coursedef")
@RestController
public class CourseDefController {

   private final CourseDefService courseDefService;

   @Autowired
   public CourseDefController(CourseDefService weeklyCoursesService) {
      this.courseDefService = weeklyCoursesService;
   }

   @RequestMapping(method = RequestMethod.GET, value = {"/courseDefDtos4Filter/{filter}", "/courseDefDtos4Filter/"})
   public List<CourseDefDto> getDaysOfTheWeek4Course(@NonNull @PathVariable(required = false) String filter) {
      return courseDefService.getCourseDefDtos4Filter(filter);
   }

   @GetMapping(path = "/allCourseLocationsDtos/")
   public List<CourseLocationDto> getCourseLocationsDtos() {
      return courseDefService.getCourseLocationsDtos();
   }

   @GetMapping(path = "/isCourseDefUpdateRunning")
   public boolean isCourseDefUpdateRunning() {
      return courseDefService.isCourseDefUpdateRunning();
   }

   @PostMapping(path = "/updateCourseDefs/")
   public int updateCourseDefs(@RequestBody List<CourseLocation> courseLocations) {
      courseDefService.updateCourseDefs(courseLocations);
      return HttpStatus.OK.value();
   }
}
