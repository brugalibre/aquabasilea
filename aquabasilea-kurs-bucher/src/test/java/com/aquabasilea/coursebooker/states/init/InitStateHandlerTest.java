package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.Course.CourseBuilder;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.course.repository.yaml.impl.YamlWeeklyCoursesRepositoryImpl;
import com.aquabasilea.util.YamlUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class InitStateHandlerTest {

   public static final String COURSE_NAME = "Test";

   @AfterEach
   public void cleanUp() {
      YamlUtil.save2File(new WeeklyCourses(), getPath2YmlFile());
   }

   @Test
   void resumePrevCourses() {
      // Given
      WeeklyCoursesRepository weeklyCoursesRepository = new YamlWeeklyCoursesRepositoryImpl(getPath2YmlFile());
      InitStateHandler initStateHandler = new InitStateHandler(getPath2YmlFile(), null);
      String currentCourse1Id = "1";
      String course2Id = "2";
      String course3Id = "3";
      String course4Id = "4";
      String course6Id = "6";
      Course currentCourse = CourseBuilder.builder()
              .withTimeOfTheDay("15:15")
              .withDayOfWeek("Freitag")
              .withCourseName("Kurs-abcd")
              .withId(currentCourse1Id)
              .build();
      WeeklyCourses weeklyCourses = new WeeklyCourses(List.of(CourseBuilder.builder()
                      .withTimeOfTheDay("15:15")
                      .withDayOfWeek("Montag")
                      .withIsPaused(true)
                      .withCourseName("Kurs-11")
                      .withId(course2Id)
                      .build(), currentCourse,
              CourseBuilder.builder()
                      .withTimeOfTheDay("15:15")
                      .withDayOfWeek("Sonntag")
                      .withCourseName("Kurs-1")
                      .withId(course3Id)
                      .build(),
              CourseBuilder.builder()
                      .withTimeOfTheDay("15:15")
                      .withDayOfWeek("Samstag")
                      .withCourseName("Kurs-1")
                      .withIsPaused(true)
                      .withId(course6Id)
                      .build(),
              CourseBuilder.builder()
                      .withTimeOfTheDay("15:15")
                      .withDayOfWeek("Mittwoch")
                      .withIsPaused(true)
                      .withCourseName("Kurs-99")
                      .withId(course4Id)
                      .build()));
      weeklyCoursesRepository.save(weeklyCourses);

      // When
      initStateHandler.resumeCoursesUntil(currentCourse);

      // Then
      weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      Optional<Course> course4Id1 = getCourse4Id(course4Id, weeklyCourses);
      Optional<Course> course2Id1 = getCourse4Id(course2Id, weeklyCourses);
      Optional<Course> course6Id1 = getCourse4Id(course6Id, weeklyCourses);
      assertThat(course4Id1.isPresent(), is(true));
      assertThat(course4Id1.get().getIsPaused(), is(false));
      assertThat(course2Id1.isPresent(), is(true));
      assertThat(course2Id1.get().getIsPaused(), is(false));
      assertThat(course6Id1.isPresent(), is(true));
      assertThat(course6Id1.get().getIsPaused(), is(true));
   }

   private static Optional<Course> getCourse4Id(String course4Id, WeeklyCourses weeklyCourses) {
      return weeklyCourses.getCourses()
              .stream()
              .filter(course -> course.getId().equals(course4Id))
              .findFirst();
   }

   private static String getPath2YmlFile() {
      Path resourceDirectory = Paths.get("src", "test", "resources");
      return resourceDirectory.toFile().getAbsolutePath() + "/courses/testWeeklyCourses.yml";
   }
}