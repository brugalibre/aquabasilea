package com.aquabasilea.rest.api.coursedef;

import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.rest.service.coursedef.CourseDefRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/activfitness/v1/course-booker/coursedef")
@RestController
public class CourseDefController {

   private final CourseDefRestService courseDefRestService;
   private final IUserProvider userProvider;

   @Autowired
   public CourseDefController(CourseDefRestService courseDefRestService, IUserProvider userProvider) {
      this.courseDefRestService = courseDefRestService;
      this.userProvider = userProvider;
   }

   @GetMapping(path = "/courseDefDtos4Filter")
   public List<CourseDefDto> getCourseDefDtos() {
      return courseDefRestService.getCourseDefDtos(userProvider.getCurrentUserId());
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
   public int updateCourseDefs(@RequestBody List<String> courseLocationIds) {
      courseDefRestService.updateCourseDefs(userProvider.getCurrentUserId(), courseLocationIds);
      return HttpStatus.OK.value();
   }
}
