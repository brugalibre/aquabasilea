package com.aquabasilea.coursebooker.service.statistics;

import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.aquabasilea.coursebooker.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.coursedef.model.CourseDef;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
      Statistics statistics = getStatisticsDto(userId);
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
      Statistics statistics = getStatisticsDto(userId);
      statistics.setNextCourseDefUpdate(nextCourseDefUpdate);
      statisticsRepository.save(statistics);
   }

   /**
    * Increments the counter for the failed bookings by one
    *
    * @param userId the id of the {@link User}
    */
   public void incrementFailedBookings(String userId) {
      Statistics statistics = getStatisticsDto(userId);
      statistics.setBookingFailedCounter(statistics.getBookingFailedCounter() + 1);
      statisticsRepository.save(statistics);
   }

   /**
    * Increments the counter for the successful bookings by one
    *
    * @param userId the id of the {@link User}
    */
   public void incrementSuccessfulBookings(String userId) {
      Statistics statistics = getStatisticsDto(userId);
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
   public Statistics getStatisticsDto(String userId) {
      return statisticsRepository.getByUserId(userId);
   }

   /**
    * @param userId the id of the {@link User}
    * @return <code>true</code> if there was never a course-def update before or if it is too long ago. Otherwise returns <code>false</code>
    */
   public boolean needsCourseDefUpdate(String userId) {
      return getStatisticsDto(userId).needsCourseDefUpdate();
   }
}
