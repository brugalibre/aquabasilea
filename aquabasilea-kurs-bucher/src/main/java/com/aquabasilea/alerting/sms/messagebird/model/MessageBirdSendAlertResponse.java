package com.aquabasilea.alerting.sms.messagebird.model;

import com.aquabasilea.alerting.sms.model.response.CommonAlertResponse;
import com.messagebird.objects.MessageResponse;

public class MessageBirdSendAlertResponse extends CommonAlertResponse {

   public MessageBirdSendAlertResponse(int status, Object responseEntity) {
      super(status, responseEntity);
   }

   public static MessageBirdSendAlertResponse of(MessageResponse response) {
      return new MessageBirdSendAlertResponse(200, response.getBody());
   }
}
