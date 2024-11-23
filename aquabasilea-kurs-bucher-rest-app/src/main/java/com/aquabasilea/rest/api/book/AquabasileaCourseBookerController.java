package com.aquabasilea.rest.api.book;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.coursebooker.CourseBookingStateDto;
import com.aquabasilea.rest.model.coursebooker.cancel.CourseCancelResultDto;
import com.aquabasilea.rest.service.coursebooker.AquabasileaCourseBookerRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/activfitness/v1/course-booker")
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
   public CourseCancelResultDto cancelCourse(@PathVariable String bookingId) {
      return aquabasileaCourseBookerRestService.cancelCourse(userProvider.getCurrentUserId(), bookingId);
   }

   /**
    * Does a dry-run booking of the {@link Course} with the given id.
    * Additionally, all consumers are notified about the result
    *
    * @param courseId the id of the {@link Course} to do the dry-run booking
    */
   @PutMapping(path = "/bookCourseDryRun/{courseId}")
   public void bookCourseDryRun(@PathVariable String courseId) {
      aquabasileaCourseBookerRestService.bookCourseDryRun(userProvider.getCurrentUserId(), courseId);
   }

   /**
    * Books the of the {@link Course}. No consumers are notified about the result
    *
    * @param courseId the id of the {@link Course} to book
    */
   @PutMapping(path = "/bookCourse/{courseId}")
   public void bookCourse(@PathVariable String courseId) {
      aquabasileaCourseBookerRestService.bookCourse(userProvider.getCurrentUserId(), courseId);
   }
}
