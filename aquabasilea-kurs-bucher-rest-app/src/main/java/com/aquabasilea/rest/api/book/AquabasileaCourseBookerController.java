package com.aquabasilea.rest.api.book;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.coursebooker.CourseBookingStateDto;
import com.aquabasilea.rest.service.coursebooker.AquabasileaCourseBookerRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/aquabasilea-course-booker")
@RestController
public class AquabasileaCourseBookerController {

   private final AquabasileaCourseBookerRestService aquabasileaCourseBookerRestService;
   private final IUserProvider userProvider;

   @Autowired
   public AquabasileaCourseBookerController(AquabasileaCourseBookerRestService aquabasileaCourseBookerRestService, IUserProvider userProvider) {
      this.aquabasileaCourseBookerRestService = aquabasileaCourseBookerRestService;
      this.userProvider = userProvider;
   }

   @PutMapping(path = "/pauseOrResume")
   public int pauseOrResume() {
      aquabasileaCourseBookerRestService.pauseOrResume(userProvider.getCurrentUserId());
      return HttpStatus.OK.value();
   }

   @GetMapping(path = "/state")
   public CourseBookingStateDto getStatus() {
      return aquabasileaCourseBookerRestService.getCourseBookingStateDto(userProvider.getCurrentUserId());
   }

   @GetMapping(path = "/booked-courses")
   public List<CourseDto> getBookedCourses() {
      return aquabasileaCourseBookerRestService.getBookedCourses(userProvider.getCurrentUserId());
   }

   @DeleteMapping(path = "/cancel/{bookingId}")
   public int cancelCourse(@PathVariable String bookingId) {
      aquabasileaCourseBookerRestService.cancelCourse(userProvider.getCurrentUserId(), bookingId);
      return HttpStatus.OK.value();
   }

   /**
    * Does a dry-run booking of the current Course. Additionally, all consumers are notified about the result
    *
    * @param courseId the id of the {@link Course} to book
    */
   @PutMapping(path = "/bookCourseDryRun/{courseId}")
   public void bookCourseDryRun(@PathVariable String courseId) {
      aquabasileaCourseBookerRestService.bookCourseDryRun(userProvider.getCurrentUserId(), courseId);
   }
}
