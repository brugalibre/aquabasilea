package com.aquabasilea.service.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResultDetails;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AquabasileaCourseBookerServiceTest {

   @Test
   void cancelCourse4PhoneNr_NoUser4PhoneNR() {
      // Given
      UserRepository userRepository = getMockedUserRepository(null);
      AquabasileaCourseBooker aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = getAquabasileaCourseBookerHolder(aquabasileaCourseBooker, "USER_ID");
      AquabasileaCourseBookerService aquabasileaCourseBookerService = new AquabasileaCourseBookerService(aquabasileaCourseBookerHolder, userRepository);

      // When
      CourseCancelResultDetails courseCancelResultDetails = aquabasileaCourseBookerService.cancelCourse4PhoneNr("234", null);

      // Then
      verify(aquabasileaCourseBooker, never()).cancelBookedCourse(any());
      assertThat(courseCancelResultDetails.courseCancelResult()).isEqualTo(CourseCancelResult.COURSE_NOT_CANCELED);
   }

   @Test
   void cancelCourse4PhoneNr_OneBookedCourse() {
      // Given
      String bookingIdTac = "12345";
      String userId = "234";
      UserRepository userRepository = getMockedUserRepository(new User(userId, "name", "pwd", List.of(), List.of()));
      List<Course> bookedCourses = getBookedCourses(bookingIdTac);
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker(bookedCourses);
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = getAquabasileaCourseBookerHolder(aquabasileaCourseBooker, userId);
      AquabasileaCourseBookerService aquabasileaCourseBookerService = new AquabasileaCourseBookerService(aquabasileaCourseBookerHolder, userRepository);

      // When
      CourseCancelResultDetails courseCancelResultDetails = aquabasileaCourseBookerService.cancelCourse4PhoneNr(userId, null);

      // Then
      verify(aquabasileaCourseBooker).cancelBookedCourse(eq(bookingIdTac));
      assertThat(courseCancelResultDetails.courseCancelResult()).isEqualTo(CourseCancelResult.COURSE_CANCELED);
   }

   @Test
   void cancelCourse4PhoneNr_TwoBookedCourses() {
      // Given
      String bookingIdTac1 = "12345";
      String bookingIdTac2 = "65418";
      String userId = "234";
      UserRepository userRepository = getMockedUserRepository(new User(userId, "name", "pwd", List.of(), List.of()));
      List<Course> bookedCourses = getBookedCourses(bookingIdTac1, bookingIdTac2);
      Course course2Cancel = bookedCourses.get(0);
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker(bookedCourses);
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = getAquabasileaCourseBookerHolder(aquabasileaCourseBooker, userId);
      AquabasileaCourseBookerService aquabasileaCourseBookerService = new AquabasileaCourseBookerService(aquabasileaCourseBookerHolder, userRepository);

      // When
      CourseCancelResultDetails courseCancelResultDetails = aquabasileaCourseBookerService.cancelCourse4PhoneNr(userId, course2Cancel.getCourseName());

      // Then
      verify(aquabasileaCourseBooker).cancelBookedCourse(eq(course2Cancel.getBookingIdTac()));
      assertThat(courseCancelResultDetails.courseCancelResult()).isEqualTo(CourseCancelResult.COURSE_CANCELED);
   }

   @Test
   void cancelCourse4PhoneNr_TwoBookedCourses_ButNoCourseNameProvided() {
      // Given
      String bookingIdTac1 = "1";
      String bookingIdTac2 = "3";
      String userId = "4564";
      UserRepository userRepository = getMockedUserRepository(new User(userId, "hans", "pwd", List.of(), List.of()));
      List<Course> bookedCourses = getBookedCourses(bookingIdTac1, bookingIdTac2);
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker(bookedCourses);
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = getAquabasileaCourseBookerHolder(aquabasileaCourseBooker, userId);
      AquabasileaCourseBookerService aquabasileaCourseBookerService = new AquabasileaCourseBookerService(aquabasileaCourseBookerHolder, userRepository);

      // When
      CourseCancelResultDetails courseCancelResultDetails = aquabasileaCourseBookerService.cancelCourse4PhoneNr(userId, null);

      // Then
      verify(aquabasileaCourseBooker, never()).cancelBookedCourse(any());
      assertThat(courseCancelResultDetails.courseCancelResult()).isEqualTo(CourseCancelResult.COURSE_NOT_CANCELED);
   }

   private static List<Course> getBookedCourses(String... bookingIdTacs) {
      List<Course> bookedCourses = new ArrayList<>();
      for (String bookingIdTac : bookingIdTacs) {
         Course bookedCourse = new Course();
         bookedCourse.setCourseName(UUID.randomUUID().toString());
         bookedCourse.setBookingIdTac(bookingIdTac);
         bookedCourses.add(bookedCourse);
      }
      return bookedCourses;
   }

   private static AquabasileaCourseBooker getAquabasileaCourseBooker(List<Course> bookedCourses) {
      AquabasileaCourseBooker aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
      when(aquabasileaCourseBooker.getBookedCourses()).thenReturn(bookedCourses);
      when(aquabasileaCourseBooker.cancelBookedCourse(any())).thenReturn(new CourseCancelResultDetails(CourseCancelResult.COURSE_CANCELED, null));
      return aquabasileaCourseBooker;
   }

   private static UserRepository getMockedUserRepository(User user) {
      UserRepository userRepository = mock(UserRepository.class);
      when(userRepository.findByPhoneNr(any())).thenReturn(Optional.ofNullable(user));
      return userRepository;
   }

   private static AquabasileaCourseBookerHolder getAquabasileaCourseBookerHolder(AquabasileaCourseBooker aquabasileaCourseBooker, String userId) {
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = spy(new AquabasileaCourseBookerHolder());
      aquabasileaCourseBookerHolder.putForUserId(userId, aquabasileaCourseBooker);
      return aquabasileaCourseBookerHolder;
   }
}