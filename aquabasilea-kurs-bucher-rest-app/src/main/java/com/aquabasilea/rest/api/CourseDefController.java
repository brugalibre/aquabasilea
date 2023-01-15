package com.aquabasilea.rest.api;

import com.aquabasilea.coursebooker.model.course.CourseLocation;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.rest.service.coursedef.CourseDefRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/aquabasilea-course-booker/coursedef")
@RestController
public class CourseDefController {

   private final CourseDefRestService courseDefRestService;
   private final IUserProvider userProvider;

   @Autowired
   public CourseDefController(CourseDefRestService courseDefRestService, IUserProvider userProvider) {
      this.courseDefRestService = courseDefRestService;
      this.userProvider = userProvider;
   }

   @RequestMapping(method = RequestMethod.GET, value = {"/courseDefDtos4Filter/{filter}", "/courseDefDtos4Filter/", "/courseDefDtos4Filter"})
   public List<CourseDefDto> getDaysOfTheWeek4Course(@NonNull @PathVariable(required = false) String filter) {
      return courseDefRestService.getCourseDefDtos4Filter(userProvider.getCurrentUserId(), filter);
   }

   @GetMapping(path = "/allCourseLocationsDtos")
   public List<CourseLocationDto> getCourseLocationsDtos() {
      return courseDefRestService.getCourseLocationsDtosByUserId(userProvider.getCurrentUserId());
   }

   @GetMapping(path = "/isCourseDefUpdateRunning")
   public boolean isCourseDefUpdateRunning() {
      return courseDefRestService.isCourseDefUpdateRunning(userProvider.getCurrentUserId());
   }

   @RequestMapping(value = "/updateAll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
   public int updateCourseDefs(@RequestBody List<CourseLocation> courseLocations) {
      courseDefRestService.updateCourseDefs(userProvider.getCurrentUserId(), courseLocations);
      return HttpStatus.OK.value();
   }
}
