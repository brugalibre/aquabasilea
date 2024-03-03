package com.aquabasilea.domain.healthcheck.model.repository;

import com.aquabasilea.domain.healthcheck.mapper.HealthCheckEntityMapperImpl;
import com.aquabasilea.domain.healthcheck.model.HealthCheckDo;
import com.aquabasilea.persistence.healthcheck.HealthCheckEntity;
import com.aquabasilea.persistence.healthcheck.dao.HealthCheckDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;

public class HealthCheckRepository extends CommonDomainRepositoryImpl<HealthCheckDo, HealthCheckEntity, HealthCheckDao> {
    public HealthCheckRepository(HealthCheckDao healthCheckDao) {
        super(healthCheckDao, new HealthCheckEntityMapperImpl());
    }

    /**
     * @return the one and only instance of a {@link HealthCheckDo}
     */
    public HealthCheckDo get() {
        return getAll().stream()
                .findFirst()
                .orElseThrow(() -> new NoDomainModelFoundException("No HealthCheck-Entity found!"));
    }
}
