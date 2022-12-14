package com.aquabasilea.rest.service.admin;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.admin.AdminOverviewDto;
import com.aquabasilea.rest.model.admin.Course4AdminViewDto;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.rest.service.statistics.StatisticsRestService;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
public class AdminRestService {

   private final StatisticsRestService statisticsRestService;
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final UserRepository userRepository;

   public AdminRestService(StatisticsRestService statisticsRestService, AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder,
                           UserRepository userRepository) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.statisticsRestService = statisticsRestService;
      this.userRepository = userRepository;
   }

   public AdminOverviewDto getAdminOverviewDto() {
      List<StatisticsDto> statisticsDtos = getStatisticsDtos();
      TotalBookingStats totalBookingCounter = getTotalBookingStats(statisticsDtos);
      return new AdminOverviewDto(statisticsDtos.size(), totalBookingCounter.totalBookingCounter,
              totalBookingCounter.getTotalBookingSuccessRate(),
              statisticsRestService.getDurationString(),
              getNextCourse4AdminViewDtos());
   }

   private static TotalBookingStats getTotalBookingStats(List<StatisticsDto> statisticsDtos) {
      TotalBookingStats totalBookingCounter = new TotalBookingStats();
      for (StatisticsDto statisticsDto : statisticsDtos) {
         totalBookingCounter.addTotalBookingCounter(statisticsDto.totalBookingCounter());
         totalBookingCounter.addBookingSuccessRate(statisticsDto.bookingSuccessRate());
         totalBookingCounter.incrementCounterIfNecessary(statisticsDto.totalBookingCounter());
      }
      return totalBookingCounter;
   }

   private List<Course4AdminViewDto> getNextCourse4AdminViewDtos() {
      return aquabasileaCourseBookerHolder.getUserId2AquabasileaCourseBookerMap().entrySet()
              .stream()
              .map(this::buildCourse4AdminViewDto)
              .filter(Objects::nonNull)
              .sorted(new CourseDtoAdminViewComparator())
              .toList();
   }

   private Course4AdminViewDto buildCourse4AdminViewDto(Map.Entry<String, AquabasileaCourseBooker> entry) {
      User user = userRepository.getById(entry.getKey());
      AquabasileaCourseBooker aquabasileaCourseBooker = entry.getValue();
      Course currentCourse = aquabasileaCourseBooker.getCurrentCourse();
      if (nonNull(currentCourse)) {
         return Course4AdminViewDto.of(currentCourse, user, LocaleProvider.getCurrentLocale(), aquabasileaCourseBooker.isPaused());
      }
      return null;
   }

   private List<StatisticsDto> getStatisticsDtos() {
      return userRepository.getAll()
              .stream()
              .map(User::getId)
              .map(statisticsRestService::getStatisticDtoByUserId)
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
