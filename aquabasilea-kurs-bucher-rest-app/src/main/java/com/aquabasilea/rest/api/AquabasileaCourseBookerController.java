package com.aquabasilea.rest.api;

import com.aquabasilea.rest.model.CourseBookingStateDto;
import com.aquabasilea.rest.service.AquabasileaCourseBookerService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
