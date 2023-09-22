package com.aquabasilea.rest.api.statistics;

import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.rest.service.statistics.StatisticsRestService;
import com.brugalibre.common.security.user.service.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/aquabasilea-course-booker")
@RestController
public class StatisticsController {

   private final StatisticsRestService statisticsRestService;
   private final IUserProvider userProvider;

   @Autowired
   public StatisticsController(StatisticsRestService statisticsRestService, IUserProvider userProvider) {
      this.statisticsRestService = statisticsRestService;
      this.userProvider = userProvider;
   }

   @GetMapping(path = "/statistics")
   public StatisticsDto getStatisticsDto() {
      return statisticsRestService.getStatisticDtoByUserId(userProvider.getCurrentUserId());
   }
}
