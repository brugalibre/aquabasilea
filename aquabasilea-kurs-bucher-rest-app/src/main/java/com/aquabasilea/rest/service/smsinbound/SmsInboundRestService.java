package com.aquabasilea.rest.service.smsinbound;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsInboundRestService {

   private final AquabasileaCourseBookerService aquabasileaCourseBookerService;

   @Autowired
   public SmsInboundRestService(AquabasileaCourseBookerService aquabasileaCourseBookerService) {
      this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
   }

   /**
    * Cancels the course for the user associated with the given phone-nr
    *
    * @param phoneNr    the phone-Nr of the user for whom the given booking is going to be canceled
    * @param smsCommand the sms text which was sending by the user. It contains an optional name of the course to cancel.
    *                   Only required if there are more than one booked courses
    * @return a {@link CourseCancelResult}
    */
   public CourseCancelResult cancelCourse4PhoneNr(String phoneNr, String smsCommand) {
      String courseName = smsCommand.replace(TextResources.CANCEL_BOOKED_COURSE_SMS_CODE, "").trim();
      return aquabasileaCourseBookerService.cancelCourse4PhoneNr(phoneNr, courseName);
   }
}
