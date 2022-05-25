package com.aquabasilea.rest.api;

import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.rest.service.statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/aquabasilea-course-booker/statistics")
@RestController
public class StatisticsController {

   private final StatisticsService statisticsService;

   @Autowired
   public StatisticsController(StatisticsService statisticsService) {
      this.statisticsService = statisticsService;
   }

   @GetMapping(path = "/getStatistics")
   public StatisticsDto getStatisticsDto() {
      return statisticsService.getStatisticDto();
   }
}
