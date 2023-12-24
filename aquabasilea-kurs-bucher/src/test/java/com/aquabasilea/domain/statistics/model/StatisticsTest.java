package com.aquabasilea.domain.statistics.model;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsTest {

   public static final String USER_ID_1 = "userId1";

   @Test
   void needsCourseDefUpdate_NoLastUpdate() {
      // Given
      Statistics statistics = new Statistics(USER_ID_1, Clock.systemDefaultZone());

      // When
      boolean actualIsUpdateNeeded = statistics.needsCourseDefUpdate();

      // Then
      assertThat(actualIsUpdateNeeded).isTrue();
   }

   @Test
   void needsCourseDefUpdate_LastUpdateIsLastYear() {
      // Given
      LocalDateTime lastCourseDefUpdate = LocalDateTime.of(2023, 12, 23, 12, 40);
      Clock clock = Clock.fixed(
              Instant.parse("2024-01-01T15:40:00Z"),
              ZoneOffset.systemDefault());
      Statistics statistics = new Statistics(USER_ID_1, clock);
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);

      // When
      boolean actualIsUpdateNeeded = statistics.needsCourseDefUpdate();

      // Then
      assertThat(actualIsUpdateNeeded).isTrue();
   }

   @Test
   void needsCourseDefUpdate_LastUpdateIsEarlierInSameYear() {
      // Given
      LocalDateTime lastCourseDefUpdate = LocalDateTime.of(2024, 6, 23, 11, 40);
      Clock clock = Clock.fixed(
              Instant.parse("2024-12-23T15:40:00Z"),
              ZoneOffset.systemDefault());
      Statistics statistics = new Statistics(USER_ID_1, clock);
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);

      // When
      boolean actualIsUpdateNeeded = statistics.needsCourseDefUpdate();

      // Then
      assertThat(actualIsUpdateNeeded).isTrue();
   }
}