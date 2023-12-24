package com.aquabasilea.migrosapi.api.service.configuration;

public enum MigrosApiBookStrategy {
   /**
    * No retry
    */
   NO_RETRY,
   /**
    * Retry on certain, retryable, errors
    */
   RETRY,
}
