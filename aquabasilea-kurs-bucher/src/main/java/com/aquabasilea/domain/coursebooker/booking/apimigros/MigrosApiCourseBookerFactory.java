package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.book.api.MigrosBookContext;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiBookCourseResponse;
import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;
import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class MigrosApiCourseBookerFactory {

   private final MigrosApiProvider migrosApiProvider;

   public MigrosApiCourseBookerFactory(MigrosApiProvider migrosApiProvider) {
      this.migrosApiProvider = migrosApiProvider;
   }

   /**
    * Creates a {@link AquabasileaCourseBookerFacade} which uses the {@link MigrosApi} for the actual booking or a dry run.
    * <b>Note:</b> For each call we need to create a new instance!
    *
    * @param userName                                the user's name
    * @param userPassword                            the user's password supplier
    * @param duration2WaitUntilCourseBecomesBookable the {@link Duration} which the {@link MigrosApi} waits until the course is bookable
    * @return a {@link AquabasileaCourseBookerFacade} based on the {@link MigrosApi}
    */
   public AquabasileaCourseBookerFacade createMigrosApiCourseBookerImpl(String userName, Supplier<char[]> userPassword, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      MigrosApi migrosApi = migrosApiProvider.getNewMigrosApi();
      return courseBookContainer -> {
         CourseBookDetails courseBookDetails = courseBookContainer.courseBookDetails();
         AuthenticationContainer authenticationContainer = new AuthenticationContainer(userName, userPassword);
         MigrosApiBookCourseRequest migrosApiBookCourseRequest = getMigrosApiBookCourseRequest(duration2WaitUntilCourseBecomesBookable, courseBookContainer);
         MigrosApiBookCourseResponse migrosApiBookCourseResponse = migrosApi.bookCourse(authenticationContainer, migrosApiBookCourseRequest);
         return mapApiResponse2CourseBookingEndResult(courseBookDetails.courseName(), migrosApiBookCourseResponse);
      };
   }

   private static CourseBookingEndResult mapApiResponse2CourseBookingEndResult(String courseName, MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return CourseBookingEndResult.CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(mapMigrosCourseBookResult(migrosApiBookCourseResponse))
              .withCourseName(courseName)
              .withErrors(map2Errors(migrosApiBookCourseResponse))
              .build();
   }

   private static List<String> map2Errors(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return StringUtils.isNotEmpty(migrosApiBookCourseResponse.errorMsg()) ? List.of(migrosApiBookCourseResponse.errorMsg()) : List.of();
   }

   private static CourseClickedResult mapMigrosCourseBookResult(MigrosApiBookCourseResponse migrosApiBookCourseResponse) {
      return switch (migrosApiBookCourseResponse.courseBookResult()) {
         case COURSE_NOT_BOOKED, COURSE_BOOKING_DRY_RUN_FAILED -> CourseClickedResult.COURSE_NOT_BOOKABLE;
         case COURSE_NOT_BOOKABLE_FULLY_BOOKED -> CourseClickedResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED;
         case COURSE_BOOKED -> CourseClickedResult.COURSE_BOOKED;
         case COURSE_BOOKING_DRY_RUN_SUCCESSFUL -> CourseClickedResult.COURSE_BOOKING_ABORTED;
         case COURSE_NOT_SELECTED_EXCEPTION_OCCURRED -> CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
      };
   }

   private MigrosApiBookCourseRequest getMigrosApiBookCourseRequest(Supplier<Duration> duration2WaitUntilCourseBecomesBookable, CourseBookContainer courseBookContainer) {
      return mapCourseBookDetails2ApiRequest(courseBookContainer.courseBookDetails(),
              courseBookContainer.bookingContext().dryRun(), duration2WaitUntilCourseBecomesBookable);
   }

   private static MigrosApiBookCourseRequest mapCourseBookDetails2ApiRequest(CourseBookDetails courseBookDetails, boolean dryRun,
                                                                             Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      CourseLocation courseLocation = CourseLocation.fromDisplayName(courseBookDetails.courseLocation());
      String weekDayValue = String.valueOf(courseBookDetails.courseDate().getDayOfWeek().getValue());
      MigrosBookContext migrosBookContext = new MigrosBookContext(dryRun, duration2WaitUntilCourseBecomesBookable);
      return new MigrosApiBookCourseRequest(courseBookDetails.courseName(), weekDayValue, courseLocation.getId(), migrosBookContext);
   }
}
