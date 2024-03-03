package com.aquabasilea.rest.api.healthcheck;

import com.aquabasilea.rest.model.healthcheck.HealthCheckDto;
import com.aquabasilea.rest.service.healthcheck.HealthCheckRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aquabasilea.rest.api.healthcheck.HealthCheckController.V_1_AQUABASILEA_HEALTH_CHECK;

@RequestMapping(V_1_AQUABASILEA_HEALTH_CHECK)
@RestController
public class HealthCheckController {

    public static final String V_1_AQUABASILEA_HEALTH_CHECK = "/api/activfitness/v1/healthcheck";
    private final HealthCheckRestService healthCheckRestService;

    @Autowired
    public HealthCheckController(HealthCheckRestService healthCheckRestService) {
        this.healthCheckRestService = healthCheckRestService;
    }

    @GetMapping(path = "/")
    public HealthCheckDto healthcheck() {
        return healthCheckRestService.healthCheck();
    }
}
