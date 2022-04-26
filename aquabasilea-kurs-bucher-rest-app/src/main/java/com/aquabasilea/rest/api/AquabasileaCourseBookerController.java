package com.aquabasilea.rest.api;

import com.aquabasilea.course.Course;
import com.aquabasilea.rest.model.CourseBookingStateDto;
import com.aquabasilea.rest.service.AquabasileaCourseBookerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/v1/aquabasilea-course-booker")
@RestController
public class AquabasileaCourseBookerController {

   private AquabasileaCourseBookerService aquabasileaCourseBookerService;

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
