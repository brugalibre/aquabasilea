package com.aquabasilea.notification.alertsend;

import com.brugalibre.notification.api.AlertResponse;

public record TestAlertResponse(int status, String responseEntity) implements AlertResponse {

   @Override
   public int getStatus() {
      return status;
   }

   @Override
   public Object getResponseEntity() {
      return responseEntity;
   }
}
