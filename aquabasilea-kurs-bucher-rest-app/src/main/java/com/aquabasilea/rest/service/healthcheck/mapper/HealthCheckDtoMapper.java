package com.aquabasilea.rest.service.healthcheck.mapper;

import com.aquabasilea.domain.healthcheck.model.HealthCheckResult;

public class HealthCheckDtoMapper {
    public String mapToDto(HealthCheckResult healthCheckResult) {
        return healthCheckResult.status() ? "Ok" : "Failed";
    }
}
