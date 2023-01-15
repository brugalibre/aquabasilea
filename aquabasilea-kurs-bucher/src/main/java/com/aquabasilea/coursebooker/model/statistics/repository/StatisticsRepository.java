package com.aquabasilea.coursebooker.model.statistics.repository;

import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

/**
 * The {@link StatisticsRepository} is responsible for loading and saving {@link Statistics}s
 */
public interface StatisticsRepository extends UserRelatedRepository<Statistics> {
   // no-op
}
