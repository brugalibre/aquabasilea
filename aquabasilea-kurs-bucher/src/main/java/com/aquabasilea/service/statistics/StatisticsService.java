package com.aquabasilea.service.statistics;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.StatisticsOverview;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
@Service
public class StatisticsService {

   private final StatisticsRepository statisticsRepository;

   @Autowired
   public StatisticsService(StatisticsRepository statisticsRepository) {
      this.statisticsRepository = statisticsRepository;
   }

   /**
    * Returns the {@link Statistics} instance associated with an {@link User} with the given id
    *
    * @param userId the technical id of the {@link User}
    * @return the {@link Statistics} instance associated with an {@link User} with the given id
    */
   public Statistics getByUserId(String userId) {
      return statisticsRepository.getByUserId(userId);
   }

   /**
    * Sets the {@link Statistics#getLastCourseDefUpdate()} attribut and persist the changes
    *
    * @param userId              the id of the {@link User}
    * @param lastCourseDefUpdate the new value of the lastCourseDefUpdate
    */
   public void setLastCourseDefUpdate(String userId, LocalDateTime lastCourseDefUpdate) {
      Statistics statistics = getStatisticsByUserId(userId);
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);
      statisticsRepository.save(statistics);
   }


   /**
    * Sets the {@link LocalDateTime} when the {@link CourseDef} are updated the next time
    *
    * @param userId              the id of the {@link User}
    * @param nextCourseDefUpdate the {@link LocalDateTime} when the {@link CourseDef} are updated the next time
    */
   public void setNextCourseDefUpdate(String userId, LocalDateTime nextCourseDefUpdate) {
      Statistics statistics = getStatisticsByUserId(userId);
      statistics.setNextCourseDefUpdate(nextCourseDefUpdate);
      statisticsRepository.save(statistics);
   }

   /**
    * Increments the counter for the failed bookings by one
    *
    * @param userId the id of the {@link User}
    */
   public void incrementFailedBookings(String userId) {
      Statistics statistics = getStatisticsByUserId(userId);
      statistics.setBookingFailedCounter(statistics.getBookingFailedCounter() + 1);
      statisticsRepository.save(statistics);
   }

   /**
    * Increments the counter for the successful bookings by one
    *
    * @param userId the id of the {@link User}
    */
   public void incrementSuccessfulBookings(String userId) {
      Statistics statistics = getStatisticsByUserId(userId);
      statistics.setBookingSuccessfulCounter(statistics.getBookingSuccessfulCounter() + 1);
      statisticsRepository.save(statistics);
   }

   /**
    * Returns a {@link Statistics} which belongs to the given user id
    *
    * @param userId the id of the {@link User} the id of the {@link User}
    * @return a {@link Statistics} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link Statistics} associated with the given user-id
    */
   public Statistics getStatisticsByUserId(String userId) {
      return statisticsRepository.getByUserId(userId);
   }

   /**
    * Returns a {@link StatisticsOverview} which belongs to the given user id
    *
    * @param userId the id of the {@link User} the id of the {@link User}
    * @return a {@link Statistics} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link Statistics} associated with the given user-id
    */
   public StatisticsOverview getStatisticsOverviewByUserId(String userId) {
      Statistics statistics = statisticsRepository.getByUserId(userId);
      int totalBookingCounter = statistics.getBookingSuccessfulCounter() + statistics.getBookingFailedCounter();
      return new StatisticsOverview(statistics, totalBookingCounter, getBookingSuccessRate(totalBookingCounter, statistics.getBookingSuccessfulCounter()));
   }

   private static double getBookingSuccessRate(int totalBookingCounter, int bookingSuccessfulCounter) {
      if (totalBookingCounter == 0) {
         return 0;
      }
      BigDecimal bookingSuccessfulCounterBD = BigDecimal.valueOf(bookingSuccessfulCounter);
      BigDecimal totalBookingCounterBD = BigDecimal.valueOf(totalBookingCounter);
      return bookingSuccessfulCounterBD.divide(totalBookingCounterBD, 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
   }

   /**
    * @param userId the id of the {@link User}
    * @return <code>true</code> if there was never a course-def update before or if it is too long ago. Otherwise returns <code>false</code>
    */
   public boolean needsCourseDefUpdate(String userId) {
      return getStatisticsByUserId(userId).needsCourseDefUpdate();
   }
}
