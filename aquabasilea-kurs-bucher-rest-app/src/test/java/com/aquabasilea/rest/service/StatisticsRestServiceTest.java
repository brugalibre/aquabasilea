package com.aquabasilea.rest.service;

import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.aquabasilea.coursebooker.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.rest.config.TestAquabasileaCourseBookerRestAppConfig;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.rest.service.statistics.StatisticsRestService;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = {TestAquabasileaCourseBookerRestAppConfig.class})
class StatisticsRestServiceTest {

   @Autowired
   private StatisticsRestService statisticsRestService;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private UserRepository userRepository;

   private String userId;

   @AfterEach
   public void cleanUp() {
      userRepository.deleteAll();
   }

   @BeforeEach
   public void setUp() {
      this.userId = userRepository.save(User.of("peter", "1234", MobilePhone.of("0791234567"))).id();
   }

   @Test
   void createAndSaveStatistics() {
      // Given
      Statistics statistics = new Statistics(userId);
      statistics.setBookingSuccessfulCounter(1);
      statisticsRepository.save(statistics);

      // When
      StatisticsDto statisticDto = statisticsRestService.getStatisticDtoByUserId(userId);

      // Then
      assertThat(statisticDto.bookingSuccessRate(), is(100.0));
   }
}