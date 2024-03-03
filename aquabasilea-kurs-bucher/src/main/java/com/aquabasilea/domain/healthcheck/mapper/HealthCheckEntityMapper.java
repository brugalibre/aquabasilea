package com.aquabasilea.domain.healthcheck.mapper;

import com.aquabasilea.domain.healthcheck.model.HealthCheckDo;
import com.aquabasilea.persistence.healthcheck.HealthCheckEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface HealthCheckEntityMapper extends CommonDomainModelMapper<HealthCheckDo, HealthCheckEntity> {
// no-op
}