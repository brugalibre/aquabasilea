package com.aquabasilea.persistence.healthcheck.dao;

import com.aquabasilea.persistence.healthcheck.HealthCheckEntity;
import org.springframework.data.repository.CrudRepository;

public interface HealthCheckDao extends CrudRepository<HealthCheckEntity, String> {
    // no-op
}
