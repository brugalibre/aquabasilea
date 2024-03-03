package com.aquabasilea.application.initialize.persistence.healthcheck;

import com.aquabasilea.application.initialize.api.AppInitializer;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.domain.healthcheck.model.HealthCheckDo;
import com.aquabasilea.domain.healthcheck.model.repository.HealthCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.aquabasilea.application.initialize.common.InitializationConst.HEALTH_CHECK;

@Service
@InitializeOrder(order = HEALTH_CHECK, type = {InitType.APP_STARTED})
public class HealthCheckInitializer implements AppInitializer {
    private final HealthCheckRepository healthCheckRepository;
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckInitializer.class);


    public HealthCheckInitializer(HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    @Override
    public void initializeOnAppStart() {
        if (healthCheckRepository.getAll().isEmpty()) {
            LOG.info("Creating health-check entity");
            healthCheckRepository.save(new HealthCheckDo());
        } else {
            LOG.info("Health-check entity already exists");
        }
    }
}
