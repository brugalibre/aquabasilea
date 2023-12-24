package com.aquabasilea.service.statistics;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.StatisticsOverview;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    @Test
    void testGetStatisticsOverviewByUserId() {

        // Given
        String userId = "user";
        Statistics statistics = new Statistics(userId, Clock.systemDefaultZone());
        StatisticsRepository statisticsRepository = getStatisticsRepository(userId, statistics);
        StatisticsService statisticsService = new StatisticsService(statisticsRepository);

        // When
        StatisticsOverview statisticsOverview = statisticsService.getStatisticsOverviewByUserId(userId);

        // Then
        assertThat(statisticsOverview.bookingSuccessRate(), is(0.0));
        assertThat(statisticsOverview.totalBookingCounter(), is(0));
    }

    @Test
    void testGetStatisticsOverviewByUserId_WithBookingHistory() {

        // Given
        String userId = "user";
        Statistics statistics = getStatistics(userId);
        StatisticsRepository statisticsRepository = getStatisticsRepository(userId, statistics);
        StatisticsService statisticsService = new StatisticsService(statisticsRepository);

        // When
        StatisticsOverview statisticsOverview = statisticsService.getStatisticsOverviewByUserId(userId);

        // Then
        assertThat(statisticsOverview.bookingSuccessRate(), is(66.7));
        assertThat(statisticsOverview.totalBookingCounter(), is(12));
    }

    private static StatisticsRepository getStatisticsRepository(String userId, Statistics statistics) {
        StatisticsRepository statisticsRepository = mock(StatisticsRepository.class);
        when(statisticsRepository.getByUserId(userId)).thenReturn(statistics);
        return statisticsRepository;
    }

    private static Statistics getStatistics(String userId) {
        Statistics statistics = new Statistics(userId, Clock.systemDefaultZone());
        statistics.setBookingFailedCounter(4);
        statistics.setBookingSuccessfulCounter(8);
        return statistics;
    }
}