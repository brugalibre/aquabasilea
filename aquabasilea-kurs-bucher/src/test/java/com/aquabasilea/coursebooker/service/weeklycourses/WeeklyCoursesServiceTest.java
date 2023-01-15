package com.aquabasilea.coursebooker.service.weeklycourses;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursedef.model.CourseDef;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.aquabasilea.coursebooker.model.course.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class WeeklyCoursesServiceTest {

   public static final String COURSE_NAME_WITH_COURSE_DEF = "Kurs-1";
   private static final String USER_ID = "1234";

   @Test
   void updateCoursesAfterCourseDefUpdate() {
      // Given
      LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15));
      LocalDateTime dayAfterTomorrow = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(15, 15));
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(Course.CourseBuilder.builder()
                      .withCourseDate(now)
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-99")
                      .withId("1")
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withCourse(Course.CourseBuilder.builder()
                      .withCourseDate(dayAfterTomorrow)
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName(COURSE_NAME_WITH_COURSE_DEF)
                      .withId("1")
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withUpdatedCourseDef(new CourseDef("1", USER_ID, now, MIGROS_FITNESSCENTER_AQUABASILEA, "", ""))
              .withUpdatedCourseDef(new CourseDef("2", USER_ID, dayAfterTomorrow, MIGROS_FITNESSCENTER_AQUABASILEA, COURSE_NAME_WITH_COURSE_DEF, ""))
              .build();

      // When
      tcb.weeklyCoursesService.updateCoursesAfterCourseDefUpdate(USER_ID, tcb.updatedCourseDefs);

      // Then
      assertThat(tcb.weeklyCourses.getCourses().get(0).getHasCourseDef(), is(false));
      assertThat(tcb.weeklyCourses.getCourses().get(1).getHasCourseDef(), is(true));
      verify(tcb.aquabasileaCourseBooker).refreshCourses();
      verify(tcb.weeklyCoursesRepository).save(any());
   }

   private static class TestCaseBuilder {
      private final WeeklyCoursesRepository weeklyCoursesRepository;
      private final WeeklyCourses weeklyCourses;
      private final WeeklyCoursesService weeklyCoursesService;
      private final List<CourseDef> updatedCourseDefs;
      private final AquabasileaCourseBooker aquabasileaCourseBooker;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.weeklyCourses = new WeeklyCourses();
         this.aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
         AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = new AquabasileaCourseBookerHolder();
         aquabasileaCourseBookerHolder.putForUserId(USER_ID, aquabasileaCourseBooker);
         this.weeklyCoursesService = new WeeklyCoursesService(weeklyCoursesRepository, aquabasileaCourseBookerHolder);
         this.updatedCourseDefs = new ArrayList<>();
      }

      private TestCaseBuilder withCourse(Course course) {
         this.weeklyCourses.addCourse(course);
         return this;
      }

      private TestCaseBuilder withUpdatedCourseDef(CourseDef courseDef) {
         this.updatedCourseDefs.add(courseDef);
         return this;
      }

      private TestCaseBuilder build() {
         mockWeeklyCoursesRepository();
         return this;
      }

      private void mockWeeklyCoursesRepository() {
         when(weeklyCoursesRepository.getByUserId(USER_ID)).thenReturn(weeklyCourses);
      }
   }
}