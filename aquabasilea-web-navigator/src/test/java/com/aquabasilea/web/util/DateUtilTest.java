package com.aquabasilea.web.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateUtilTest {

   @Test
   public void testGetLocalDateFromInput1() {
      // Given
      String input = "Montag 13. Juni 2022";
      LocalDate expectedLocalDate = LocalDate.of(2022, Month.JUNE, 13);

      // When
      LocalDate actualLocalDateFromInput = DateUtil.getLocalDateFromInput(input, Locale.GERMAN);

      // Then
      assertThat(actualLocalDateFromInput, is(expectedLocalDate));
   }

   @Test
   public void testGetLocalDateFromInput2() {
      // Given
      String input = "Montag 20. Juni 2022";
      LocalDate expectedLocalDate = LocalDate.of(2022, Month.JUNE, 20);

      // When
      LocalDate actualLocalDateFromInput = DateUtil.getLocalDateFromInput(input, Locale.GERMAN);

      // Then
      assertThat(actualLocalDateFromInput, is(expectedLocalDate));
   }

   @Test
   public void testGetLocalDateFromInput3() {
      // Given
      String input = "Montag 22. Juni 2022";
      LocalDate expectedLocalDate = LocalDate.of(2022, Month.JUNE, 22);

      // When
      LocalDate actualLocalDateFromInput = DateUtil.getLocalDateFromInput(input, Locale.GERMAN);

      // Then
      assertThat(actualLocalDateFromInput, is(expectedLocalDate));
   }
}