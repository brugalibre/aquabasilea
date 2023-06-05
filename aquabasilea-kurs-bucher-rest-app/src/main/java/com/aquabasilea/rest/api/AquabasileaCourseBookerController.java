package com.aquabasilea.rest.api;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.rest.model.CourseBookingStateDto;
import com.aquabasilea.rest.service.AquabasileaCourseBookerService;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.aquabasilea.coursebooker.states.CourseBookingState.BOOKING_DRY_RUN;

@RequestMapping("/api/v1/aquabasilea-course-booker")
@RestController
public class AquabasileaCourseBookerController {

   private final AquabasileaCourseBookerService aquabasileaCourseBookerService;
   private final IUserProvider userProvider;

   @Autowired
   public AquabasileaCourseBookerController(AquabasileaCourseBookerService aquabasileaCourseBookerService, IUserProvider userProvider) {
      this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
      this.userProvider = userProvider;
   }

   @PutMapping(path = "/pauseOrResume")
   public int pauseOrResume() {
      aquabasileaCourseBookerService.pauseOrResume(userProvider.getCurrentUserId());
      return HttpStatus.OK.value();
   }

   @GetMapping(path = "/state")
   public CourseBookingStateDto getStatus() {
      return aquabasileaCourseBookerService.getCourseBookingStateDto(userProvider.getCurrentUserId());
   }

   /**
    * Does a dry-run booking of the current Course. Additionally, all consumers are notified about the result
    *
    * @param courseId           the id of the {@link Course} to book
    * @return a {@link CourseBookingEndResult} with details about the booking
    */
   @PutMapping(path = "/bookCourseDryRun/{courseId}")
   public void bookCourseDryRun(@PathVariable String courseId) {
      aquabasileaCourseBookerService.bookCourseDryRun(userProvider.getCurrentUserId(), courseId);
   }
}
