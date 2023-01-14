package com.aquabasilea.alerting.consumer.impl;

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
