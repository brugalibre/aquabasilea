package com.aquabasilea.domain.coursebooker.booking.facade;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.BookingContext;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.book.request.MigrosBookContext;
import com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class AquabasileaCourseBookerFacadeFactoryTest {

   @Test
   void createNewAquabasileaCourseBookerFacade_NoErrors() {

      // Given
      String username = "username";
      Supplier<char[]> userPwdCharSup = "dsf"::toCharArray;
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(32);

      CourseLocation migrosFitnesscenterAquabasilea = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      String coursename = "coursename";
      LocalDateTime courseDate = LocalDateTime.now();
      String weekday = String.valueOf(courseDate.getDayOfWeek().getValue());
      CourseBookDetails courseBookDetails = new CourseBookDetails(coursename, "leyla", courseDate, migrosFitnesscenterAquabasilea.getCourseLocationName());
      CourseBookContainer courseBookContainer1 = new CourseBookContainer(courseBookDetails, new BookingContext(true));
      CourseBookContainer courseBookContainer2 = new CourseBookContainer(courseBookDetails, new BookingContext(false));

      AuthenticationContainer expectedAuthContainer = new AuthenticationContainer(username, userPwdCharSup);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest1 = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.getId()), new MigrosBookContext(true, durationSupplier));
      MigrosApiBookCourseRequest migrosApiBookCourseRequest2 = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.getId()), new MigrosBookContext(false, durationSupplier));

      AquabasileaCourseBookerConfig config = getAquabasileaCourseBookerConfig(AquabasileaCourseBookerType.MIGROS_API);
      MigrosApi migrosApi = mockMigrosApi(expectedAuthContainer, "", CourseBookResult.COURSE_BOOKED);
      MigrosApiProvider migrosApiProvider = new MigrosApiProvider(migrosApi);
      AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory = new AquabasileaCourseBookerFacadeFactory(migrosApiProvider, config);

      // When
      AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade1 = aquabasileaCourseBookerFacadeFactory.createNewAquabasileaCourseBookerFacade(username, userPwdCharSup, durationSupplier);
      AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade2 = aquabasileaCourseBookerFacadeFactory.createNewAquabasileaCourseBookerFacade(username, userPwdCharSup, durationSupplier);

      CourseBookingEndResult courseBookingEndResult1 = aquabasileaCourseBookerFacade1.selectAndBookCourse(courseBookContainer1);
      CourseBookingEndResult courseBookingEndResult2 = aquabasileaCourseBookerFacade2.selectAndBookCourse(courseBookContainer2);

      // Then
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest1));
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest2));
      verify(config, times(2)).refresh();

      assertThat(courseBookingEndResult1.getCourseClickedResult(), is(com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_BOOKED));
      assertThat(courseBookingEndResult2.getCourseClickedResult(), is(com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_BOOKED));
   }

   @Test
   void createNewAquabasileaCourseBookerFacade_WithErrors() {

      // Given
      String username = "username";
      Supplier<char[]> userPwdCharSup = "dsf"::toCharArray;
      Supplier<Duration> durationSupplier = () -> Duration.ofMillis(32);

      CourseLocation migrosFitnesscenterAquabasilea = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      String coursename = "coursename";
      LocalDateTime courseDate = LocalDateTime.now();
      String weekday = String.valueOf(courseDate.getDayOfWeek().getValue());
      CourseBookDetails courseBookDetails = new CourseBookDetails(coursename, "leyla", courseDate, migrosFitnesscenterAquabasilea.getCourseLocationName());
      CourseBookContainer courseBookContainer = new CourseBookContainer(courseBookDetails, new BookingContext(true));

      AuthenticationContainer expectedAuthContainer = new AuthenticationContainer(username, userPwdCharSup);
      MigrosApiBookCourseRequest migrosApiBookCourseRequest = new MigrosApiBookCourseRequest(coursename, weekday, String.valueOf(migrosFitnesscenterAquabasilea.getId()), new MigrosBookContext(true, durationSupplier));

      AquabasileaCourseBookerConfig config = getAquabasileaCourseBookerConfig(AquabasileaCourseBookerType.MIGROS_API);
      String error = "error";
      MigrosApi migrosApi = mockMigrosApi(expectedAuthContainer, error, CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED);
      MigrosApiProvider migrosApiProvider = new MigrosApiProvider(migrosApi);
      AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory = new AquabasileaCourseBookerFacadeFactory(migrosApiProvider, config);

      // When
      AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade = aquabasileaCourseBookerFacadeFactory.createNewAquabasileaCourseBookerFacade(username, userPwdCharSup, durationSupplier);
      CourseBookingEndResult courseBookingEndResult = aquabasileaCourseBookerFacade.selectAndBookCourse(courseBookContainer);

      // Then
      verify(migrosApi).bookCourse(eq(expectedAuthContainer), eq(migrosApiBookCourseRequest));
      assertThat(courseBookingEndResult.getCourseClickedResult(), is(CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED));
      assertThat(courseBookingEndResult.getErrors().size(), is(1));
      assertThat(courseBookingEndResult.getErrors().get(0), is(error));
   }

   private static MigrosApi mockMigrosApi(AuthenticationContainer expectedAuthContainer, String errorMsg, CourseBookResult courseBookResult) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      when(migrosApi.bookCourse(eq(expectedAuthContainer), any())).thenReturn(new MigrosApiBookCourseResponse(courseBookResult, errorMsg));
      return migrosApi;
   }

   private AquabasileaCourseBookerConfig getAquabasileaCourseBookerConfig(AquabasileaCourseBookerType aquabasileaCourseBookerType) {
      AquabasileaCourseBookerConfig config = mock(AquabasileaCourseBookerConfig.class);
      when(config.refresh()).thenReturn(config);
      when(config.getCourseConfigFile()).thenReturn("config/test-aquabasilea-kurs-bucher-config.yml");
      when(config.getAquabasileaCourseBookerType()).thenReturn(aquabasileaCourseBookerType);
      return config;
   }
}