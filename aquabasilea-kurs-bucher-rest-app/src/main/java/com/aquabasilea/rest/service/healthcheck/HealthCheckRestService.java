package com.aquabasilea.rest.service.healthcheck;

import com.aquabasilea.domain.healthcheck.model.HealthCheckResult;
import com.aquabasilea.domain.healthcheck.service.HealthCheckService;
import com.aquabasilea.rest.model.healthcheck.HealthCheckDto;
import com.aquabasilea.rest.service.healthcheck.mapper.HealthCheckDtoMapper;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckRestService {
    private final HealthCheckService healthCheckService;
    private final HealthCheckDtoMapper healthCheckDtoMapper;

    public HealthCheckRestService(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
        this.healthCheckDtoMapper = new HealthCheckDtoMapper();
    }

    public HealthCheckDto healthCheck() {
        HealthCheckResult healthCheckResult = healthCheckService.healthCheck();
        return new HealthCheckDto(healthCheckDtoMapper.mapToDto(healthCheckResult));
    }
}
