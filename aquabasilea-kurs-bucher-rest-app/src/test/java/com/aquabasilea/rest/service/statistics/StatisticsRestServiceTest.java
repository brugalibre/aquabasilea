package com.aquabasilea.rest.service.statistics;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.rest.config.TestAquabasileaCourseBookerRestAppConfig;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.service.statistics.StatisticsService;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

   @Test
   void testGetDurationString() {
      // Given
      LocalDateTime now = LocalDateTime.of(LocalDate.of(2023,10,4), LocalTime.of(8,35));
      Duration uptime = Duration.ofDays(401).plusHours(2).plusMinutes(15);
      RuntimeMXBean runtimeMXBean = getRuntimeMXBean(uptime);
      StatisticsRestService statisticsRestService = new StatisticsRestService(mock(StatisticsService.class), new LocaleProvider(), runtimeMXBean);

      // When
      String durationString = statisticsRestService.getDurationRelative(now);

      // Then
      assertThat(durationString, is("1 Jahre, 1 Monate, 5 Tage, 2 Stunden, 15 Minuten"));
   }

   private static RuntimeMXBean getRuntimeMXBean(Duration uptime) {
      RuntimeMXBean runtimeMXBean = mock(RuntimeMXBean.class);
      when(runtimeMXBean.getUptime()).thenReturn(uptime.toMillis());
      return runtimeMXBean;
   }
}