package com.aquabasilea.domain.coursedef.update.facade;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosCourse;
import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class CourseExtractorFacadeTest {

   @Test
   void extractAquabasileaCourses_MigrosApiConfigured() {
      // Given
      AquabasileaCourseExtractor aquabasileaCourseExtractor = mock(AquabasileaCourseExtractor.class);
      String courseInstructor = "Clara";
      String courseName = "COURSE_2";
      String userId1 = "userId1";
      AquabasileaCourseBookerConfig config = mockAquabasileaCourseBookerConfig(CourseDefExtractorType.MIGROS_API);
      MigrosApi migrosApi = mockMigrosApi(courseInstructor, courseName, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA.getId());
      CourseExtractorFacade courseExtractorFacade = new CourseExtractorFacade(() -> aquabasileaCourseExtractor, () -> migrosApi, config);

      // When
      List<CourseDef> courseDefs = courseExtractorFacade.extractAquabasileaCourses(userId1, List.of());

      // Then
      verify(config, times(2)).refresh();// once during initialization and a 2nd time bevor the call
      verify(aquabasileaCourseExtractor, never()).extractAquabasileaCourses(any());
      assertThat(courseDefs.size(), is(1));
      CourseDef courseDef = courseDefs.get(0);
      assertThat(courseDef.courseName(), is(courseName));
      assertThat(courseDef.courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(courseDef.courseInstructor(), is(courseInstructor));
      assertThat(courseDef.userId(), is(userId1));
   }

   @Test
   void extractAquabasileaCourses_AquabasileaWebExtractorConfigured() {
      // Given
      String courseName = "COURSE_1";
      String courseInstructor = "Petra";
      CourseLocation courseLocation = CourseLocation.FITNESSPARK_REGENSDORF;
      AquabasileaCourseExtractor aquabasileaCourseExtractor = mockAquabasileaCourseExtractor(courseLocation, courseName, courseInstructor);
      AquabasileaCourseBookerConfig config = mockAquabasileaCourseBookerConfig(CourseDefExtractorType.AQUABASILEA_WEB);

      MigrosApi migrosApi = mock(MigrosApi.class);
      CourseExtractorFacade courseExtractorFacade = new CourseExtractorFacade(() -> aquabasileaCourseExtractor, () -> migrosApi, config);

      // When
      String userId = "user2";
      List<CourseDef> courseDefs = courseExtractorFacade.extractAquabasileaCourses(userId, List.of(courseLocation));

      // Then
      verify(config, times(2)).refresh();// once during initialization and a 2nd time bevor the call
      verify(migrosApi, never()).getCourses(any());
      assertThat(courseDefs.size(), is(1));
      CourseDef courseDef = courseDefs.get(0);
      assertThat(courseDef.courseName(), is(courseName));
      assertThat(courseDef.courseInstructor(), is(courseInstructor));
      assertThat(courseDef.userId(), is(userId));
   }

   private static AquabasileaCourseBookerConfig mockAquabasileaCourseBookerConfig(CourseDefExtractorType courseDefExtractorType) {
      AquabasileaCourseBookerConfig config = mock(AquabasileaCourseBookerConfig.class);
      when(config.getCourseDefExtractorType()).thenReturn(courseDefExtractorType);
      when(config.refresh()).thenReturn(config);
      return config;
   }

   private static AquabasileaCourseExtractor mockAquabasileaCourseExtractor(CourseLocation courseLocation, String courseName, String courseInstructor) {
      AquabasileaCourseExtractor aquabasileaCourseExtractor = mock(AquabasileaCourseExtractor.class);
      ExtractedAquabasileaCourses value = () -> List.of(new AquabasileaCourse(LocalDateTime.now(), courseLocation.getCourseLocationName(), courseName, courseInstructor));
      when(aquabasileaCourseExtractor.extractAquabasileaCourses(any())).thenReturn(value);
      return aquabasileaCourseExtractor;
   }

   private static MigrosApi mockMigrosApi(String courseInstructor, String courseName, String centerId) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      List<MigrosCourse> courses = List.of(new MigrosCourse(LocalDateTime.now(), centerId, courseName, courseInstructor));
      MigrosApiGetCoursesResponse migrosApiGetCoursesResponse = new MigrosApiGetCoursesResponse(courses);
      when(migrosApi.getCourses(any())).thenReturn(migrosApiGetCoursesResponse);
      return migrosApi;
   }
}