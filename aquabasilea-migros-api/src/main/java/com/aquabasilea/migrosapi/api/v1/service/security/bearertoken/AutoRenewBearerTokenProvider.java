package com.aquabasilea.migrosapi.api.v1.service.security.bearertoken;

/**
 * The {@link AutoRenewBearerTokenProvider} always returns a valid bearer token. Anyway if that bearer token becomes invalid
 * (since it's the course-booker-api, which defines the actual time-out for their tokens) before the ttl is reached,
 * then a new one is retrieved.
 */
public interface AutoRenewBearerTokenProvider extends BearerTokenProvider {
}
