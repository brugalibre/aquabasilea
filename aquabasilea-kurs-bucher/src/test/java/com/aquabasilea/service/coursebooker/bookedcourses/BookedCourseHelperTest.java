package com.aquabasilea.service.coursebooker.bookedcourses;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookedCourseHelperTest {

   @Test
   void getBookedCourses() {
      // Given
      String userId = "userId";
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker(List.of(new Course()));
      AquabasileaCourseBookerHolder courseBookerHolder = getAquabasileaCourseBookerHolder(userId, aquabasileaCourseBooker);
      BookedCourseHelper bookedCourseHelper = new BookedCourseHelper(courseBookerHolder);

      // When
      List<Course> bookedCourses = bookedCourseHelper.getBookedCourses(userId);

      // Then
      Assertions.assertThat(bookedCourses.size()).isEqualTo(1);
   }

   private static AquabasileaCourseBookerHolder getAquabasileaCourseBookerHolder(String userId, AquabasileaCourseBooker aquabasileaCourseBooker) {
      AquabasileaCourseBookerHolder courseBookerHolder = new AquabasileaCourseBookerHolder();
      courseBookerHolder.putForUserId(userId, aquabasileaCourseBooker);
      return courseBookerHolder;
   }

   private static AquabasileaCourseBooker getAquabasileaCourseBooker(List<Course> bookedCourses) {
      AquabasileaCourseBooker aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
      when(aquabasileaCourseBooker.getBookedCourses()).thenReturn(bookedCourses);
      return aquabasileaCourseBooker;
   }
}