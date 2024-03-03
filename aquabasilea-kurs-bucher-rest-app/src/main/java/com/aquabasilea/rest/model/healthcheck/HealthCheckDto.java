package com.aquabasilea.rest.model.healthcheck;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record HealthCheckDto(String status) {
}
