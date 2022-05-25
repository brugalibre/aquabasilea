package com.aquabasilea.persistence.entity.statistic.dato;

import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StatisticsDao extends CrudRepository<StatisticsEntity, UUID> {
   // no
}
