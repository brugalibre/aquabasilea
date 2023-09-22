package com.aquabasilea.rest.api.smsinbound;

import com.aquabasilea.rest.model.smsinbound.SmsInboundRequestDto;
import com.aquabasilea.rest.service.smsinbound.SmsInboundRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * The {@link SmsInboundRestApiController} serves as a rest endpoint for various action taken for inbound sms
 */
@RequestMapping(SmsInboundRestApiController.API_V_1_SMS_INBOUND)
@RestController
public class SmsInboundRestApiController {

   private static final Logger LOG = LoggerFactory.getLogger(SmsInboundRestApiController.class);
   public static final String API_V_1_SMS_INBOUND = "/api/v1/sms-inbound";
   private final SmsInboundRestService smsInboundRestService;

   @Autowired
   public SmsInboundRestApiController(SmsInboundRestService smsInboundRestService) {
      this.smsInboundRestService = smsInboundRestService;
   }

   @PostMapping(path = "/cancel-course")
   public int cancelCourse4PhoneNr(@RequestBody SmsInboundRequestDto smsInboundRequestDto) {
      LOG.info("Got sms inbound request {}", smsInboundRequestDto);
      smsInboundRestService.cancelCourse4PhoneNr(smsInboundRequestDto.from(), smsInboundRequestDto.body());
      return HttpStatus.OK.value();
   }
}
