package com.aquabasilea.migrosapi.model.response.common;

import com.aquabasilea.migrosapi.http.model.response.HttpResponse;

public class CommonHttpResponse implements HttpResponse {

   private boolean successful;

   @Override
   public void setIsSuccessful(boolean successful) {
      this.successful = successful;
   }

   @Override
   public boolean isSuccessful() {
      return successful;
   }
}
