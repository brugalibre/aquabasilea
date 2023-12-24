package com.aquabasilea.migrosapi.model.getcenters.request;

import com.aquabasilea.migrosapi.api.v1.model.getcenters.request.MigrosApiGetCentersRequest;

/**
 * Defines an internal request for getting all centers provided by activefitnes
 */
public record MigrosGetCentersRequest() {
   public static MigrosGetCentersRequest of(MigrosApiGetCentersRequest migrosApiGetCentersRequest) {
      return new MigrosGetCentersRequest();
   }
}
