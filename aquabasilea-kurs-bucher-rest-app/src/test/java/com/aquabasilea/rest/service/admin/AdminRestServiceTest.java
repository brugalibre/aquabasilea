package com.aquabasilea.rest.service.admin;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.statistics.Statistics;
import com.aquabasilea.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.rest.config.TestAquabasileaCourseBookerRestAppConfig;
import com.aquabasilea.rest.model.admin.AdminOverviewDto;
import com.aquabasilea.rest.model.admin.Course4AdminViewDto;
import com.aquabasilea.rest.service.statistics.StatisticsRestService;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestAquabasileaCourseBookerRestAppConfig.class})
class AdminRestServiceTest {

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private StatisticsRestService statisticsRestService;

   @Autowired
   private UserRepository userRepository;

   private String userId1;
   private String userId2;
   private String userIdWithoutCurrentCourse;

   @AfterEach
   public void cleanUp() {
      statisticsRepository.deleteAll();
      userRepository.deleteAll();
   }

   @BeforeEach
   public void setUp() {
      this.userId1 = userRepository.save(User.of("Peter", "1234", "0791234567")).id();
      this.userId2 = userRepository.save(User.of("Hans", "1234", "0791234568")).id();
      this.userIdWithoutCurrentCourse = userRepository.save(User.of("Karl", "1234", "0791234568")).id();
      createStatistics(userIdWithoutCurrentCourse, 0, 0);
   }

   @Test
   void getAdminOverview() {
      // Given
      String courseName1 = "Kurs 1";
      String courseName2 = "Kurs 2";
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = getAquabasileaCourseBookerHolder(courseName1, courseName2);
      AdminRestService adminRestService = new AdminRestService(statisticsRestService, aquabasileaCourseBookerHolder, userRepository);
      createStatistics(userId1, 4, 2);
      createStatistics(userId2, 12, 3);

      // When
      AdminOverviewDto adminOverview = adminRestService.getAdminOverviewDto();

      // Then
      assertThat(adminOverview.bookingSuccessRate(), is(73.35));
      assertThat(adminOverview.totalBookingCounter(), is(21));
      assertThat(adminOverview.nextCurrentCourses().size(), is(2));
      assertThat(getCourse4Name(courseName1, adminOverview).isPresent(), is(true));
      assertThat(getCourse4Name(courseName2, adminOverview).isPresent(), is(true));
   }

   @Test
   void getAdminOverviewWithNoCurrentCoursesAtAll() {
      // Given
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = new AquabasileaCourseBookerHolder();
      aquabasileaCourseBookerHolder.putForUserId(userIdWithoutCurrentCourse, mock(AquabasileaCourseBooker.class));
      AdminRestService adminRestService = new AdminRestService(statisticsRestService, aquabasileaCourseBookerHolder, userRepository);
      createStatistics(userId1, 0,0);
      createStatistics(userId2, 0,0);

      // When
      AdminOverviewDto adminOverview = adminRestService.getAdminOverviewDto();

      // Then
      assertThat(adminOverview.bookingSuccessRate(), is(0.0));
      assertThat(adminOverview.totalBookingCounter(), is(0));
      assertThat(adminOverview.nextCurrentCourses().size(), is(0));
   }

   private static Optional<Course4AdminViewDto> getCourse4Name(String courseName1, AdminOverviewDto adminOverview) {
      return adminOverview.nextCurrentCourses().stream()
              .filter(course -> courseName1.equals(course.courseName()))
              .findFirst();
   }

   private AquabasileaCourseBookerHolder getAquabasileaCourseBookerHolder(String courseName1, String courseName2) {
      AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = new AquabasileaCourseBookerHolder();
      aquabasileaCourseBookerHolder.putForUserId(userId1, mockAquabasileaCourseBooker(courseName1));
      aquabasileaCourseBookerHolder.putForUserId(userId2, mockAquabasileaCourseBooker(courseName2));
      // mock one with no current course
      aquabasileaCourseBookerHolder.putForUserId(userIdWithoutCurrentCourse, mock(AquabasileaCourseBooker.class));
      return aquabasileaCourseBookerHolder;
   }

   private static AquabasileaCourseBooker mockAquabasileaCourseBooker(String courseName1) {
      AquabasileaCourseBooker aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
      when(aquabasileaCourseBooker.getCurrentCourse()).thenReturn(buildCourse(courseName1));
      return aquabasileaCourseBooker;
   }

   private static Course buildCourse(String courseName) {
      return Course.CourseBuilder.builder()
              .withCourseLocation(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA)
              .withCourseInstructor("Petra")
              .withCourseName(courseName)
              .withCourseDate(LocalDateTime.now())
              .build();
   }

   private void createStatistics(String userId, int bookingSuccessfulCounter, int bookingFailedCounter) {
      Statistics statistics = new Statistics(userId);
      statistics.setBookingSuccessfulCounter(bookingSuccessfulCounter);
      statistics.setBookingFailedCounter(bookingFailedCounter);
      statisticsRepository.save(statistics);
   }
}