package com.aquabasilea.alerting.sms.clicksend.data;

import com.aquabasilea.alerting.sms.model.response.CommonAlertResponse;

import jakarta.ws.rs.core.Response;

public class ClickSendAlertResponse extends CommonAlertResponse {

   public ClickSendAlertResponse(int status, Object responseEntity) {
      super(status, responseEntity);
   }

   public static ClickSendAlertResponse of(Response response) {
      return new ClickSendAlertResponse(response.getStatus(), response.getStatusInfo());
   }
}
