package com.aquabasilea.model.statistics.repository;

import com.aquabasilea.model.statistics.Statistics;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

/**
 * The {@link StatisticsRepository} is responsible for loading and saving {@link Statistics}s
 */
public interface StatisticsRepository extends UserRelatedRepository<Statistics> {
   // no-op
}
