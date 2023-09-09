package com.aquabasilea.service.admin;

import com.aquabasilea.domain.admin.model.AdminOverview;
import com.aquabasilea.domain.admin.model.Course4AdminView;
import com.aquabasilea.domain.admin.model.Course4AdminViewComparator;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.statistics.model.StatisticsOverview;
import com.aquabasilea.service.statistics.StatisticsService;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
public class AdminService {
    private final StatisticsService statisticsService;
    private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
    private final UserRepository userRepository;

    public AdminService(StatisticsService statisticsService, AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder,
                        UserRepository userRepository) {
        this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
        this.statisticsService = statisticsService;
        this.userRepository = userRepository;
    }

    private static TotalBookingStats getTotalBookingStats(List<StatisticsOverview> statisticsOverviews) {
        TotalBookingStats totalBookingCounter = new TotalBookingStats();
        for (StatisticsOverview statisticsOverview : statisticsOverviews) {
            totalBookingCounter.addTotalBookingCounter(statisticsOverview.totalBookingCounter());
            totalBookingCounter.addBookingSuccessRate(statisticsOverview.bookingSuccessRate());
            totalBookingCounter.incrementCounterIfNecessary(statisticsOverview.totalBookingCounter());
        }
        return totalBookingCounter;
    }

    public AdminOverview getAdminOverviewDto() {
        List<StatisticsOverview> statisticsOverviews = getStatisticsOverviews();
        TotalBookingStats totalBookingCounter = getTotalBookingStats(statisticsOverviews);
        return new AdminOverview(statisticsOverviews.size(), totalBookingCounter.totalBookingCounter,
                totalBookingCounter.getTotalBookingSuccessRate(),
                getNextCourse4AdminViewDtos());
    }

    private List<Course4AdminView> getNextCourse4AdminViewDtos() {
        return aquabasileaCourseBookerHolder.getUserId2AquabasileaCourseBookerMap().entrySet()
                .stream()
                .map(this::buildCourse4AdminViewDto)
                .filter(Objects::nonNull)
                .sorted(new Course4AdminViewComparator())
                .toList();
    }

    private Course4AdminView buildCourse4AdminViewDto(Map.Entry<String, AquabasileaCourseBooker> entry) {
        User user = userRepository.getById(entry.getKey());
        AquabasileaCourseBooker aquabasileaCourseBooker = entry.getValue();
        Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
        if (nonNull(currentCourse)) {
            return Course4AdminView.of(currentCourse, user, aquabasileaCourseBooker.isPaused());
        }
        return null;
    }

    private List<StatisticsOverview> getStatisticsOverviews() {
        return userRepository.getAll()
                .stream()
                .map(User::getId)
                .map(statisticsService::getStatisticsOverviewByUserId)
                .toList();
    }

    private static class TotalBookingStats {
        int totalBookingCounter = 0;
        int aquabasileaCourseBookersWithRuns = 0;
        double totalBookingSuccessRate = 0.0;

        private void addTotalBookingCounter(int bookingSuccessRate) {
            this.totalBookingCounter = this.totalBookingCounter + bookingSuccessRate;
        }

        private void addBookingSuccessRate(double bookingSuccessRate) {
            this.totalBookingSuccessRate = this.totalBookingSuccessRate + bookingSuccessRate;
        }

        private void incrementCounterIfNecessary(int totalBookingCounter) {
            if (totalBookingCounter > 0) {
                // in order to get the absolut percentage of all bookers, we must not count those, which hasn't booked yet
                aquabasileaCourseBookersWithRuns++;
            }
        }

        private double getTotalBookingSuccessRate() {
            if (aquabasileaCourseBookersWithRuns == 0) {
                return 0;
            }
            return totalBookingSuccessRate / aquabasileaCourseBookersWithRuns;
        }
    }
}
