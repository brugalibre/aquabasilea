package com.aquabasilea.rest.api;

import com.aquabasilea.rest.model.CourseBookingStateDto;
import com.aquabasilea.rest.service.AquabasileaCourseBookerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/aquabasilea-course-booker")
@RestController
public class AquabasileaCourseBookerController {

   private final AquabasileaCourseBookerService aquabasileaCourseBookerService;

   @Autowired
   public AquabasileaCourseBookerController(AquabasileaCourseBookerService aquabasileaCourseBookerService) {
      this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
   }

   @PostMapping(path = "/pauseOrResume")
   public int pauseOrResume() {
      aquabasileaCourseBookerService.pauseOrResume();
      return HttpStatus.OK.value();
   }

   @GetMapping(path = "/state")
   public CourseBookingStateDto getStatus() {
      return aquabasileaCourseBookerService.getCourseBookingStateDto();
   }
}
