package com.aquabasilea.notification.alertsend;

import com.brugalibre.notification.api.v1.model.AlertSendResponse;

public record TestAlertResponse(int status, String responseEntity) implements AlertSendResponse {

   @Override
   public int getStatus() {
      return status;
   }

   @Override
   public Object getResponseEntity() {
      return responseEntity;
   }
}
