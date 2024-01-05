package com.aquabasilea.rest.model.course.weeklycourses.mapper;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.course.mapper.CourseDtoMapper;
import com.aquabasilea.rest.model.course.mapper.WeeklyCoursesDtoMapper;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aquabasilea.test.TestConst.FITNESSPARK_HEUWAAGE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

class WeeklyCoursesDtoMapperTest {

   @Test
   void getWeeklyCoursesDto() {

      // Given
      CourseLocationRepository courseLocationRepository = mock(CourseLocationRepository.class);
      CourseDtoMapper courseDtoMapper = new CourseDtoMapper(courseLocationRepository, new LocaleProvider());
      WeeklyCoursesDtoMapper weeklyCoursesDtoMapper = new WeeklyCoursesDtoMapper(courseDtoMapper);

      String firstCourseId = "1";
      String secondCourseId = "2";
      String thirdCourseId = "3";
      String lastCourseId = "4";
      LocalDateTime firstCourseDate = LocalDateTime.of(2022, 12, 12, 12, 15)
              .plusDays(1);
      LocalDateTime secondCourseDate = firstCourseDate
              .plusMinutes(10);
      LocalDateTime thirdCourseDate = firstCourseDate
              .plusDays(1);
      LocalDateTime fourthCourseDate = firstCourseDate
              .plusDays(7);
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course currentCourse = CourseBuilder.builder()
              .withCourseDate(thirdCourseDate)
              .withCourseName("Kurs-abc1")
              .withId(lastCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("peter")
              .build();
      List<Course> courses = new ArrayList<>(List.of(CourseBuilder.builder()
              .withCourseDate(secondCourseDate)
              .withCourseName("Kurs-abc2")
              .withId(secondCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("Heinz")
              .build(), currentCourse, CourseBuilder.builder()
              .withCourseDate(firstCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(firstCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("Heinz")
              .build(), CourseBuilder.builder()
              .withCourseDate(fourthCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(thirdCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("Heinz")
              .build()));
      Collections.shuffle(courses);
      weeklyCourses.setCourses(courses);

      // When
      WeeklyCoursesDto weeklyCoursesDto = weeklyCoursesDtoMapper.mapToWeeklyCourseDto(weeklyCourses, currentCourse);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).id(), is(lastCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).isCurrentCourse(), is(true));
   }

   @Test
   void getWeeklyCoursesDto2() {

      // Given
      CourseLocationRepository courseLocationRepository = mock(CourseLocationRepository.class);
      CourseDtoMapper courseDtoMapper = new CourseDtoMapper(courseLocationRepository, new LocaleProvider());
      WeeklyCoursesDtoMapper weeklyCoursesDtoMapper = new WeeklyCoursesDtoMapper(courseDtoMapper);
      String firstCourseId = "1";
      String secondCourseId = "2";
      String thirdCourseId = "3";
      String fourthCourseId = "4";
      String fifthCourseId = "5";
      String sixthCourseId = "6";
      LocalDateTime firstCourseDate = LocalDateTime.of(2022, 12, 12, 18, 15, 0);
      LocalDateTime secondCourseDate = firstCourseDate
              .plusMinutes(30);
      LocalDateTime thirdCourseDate = firstCourseDate
              .plusDays(2);
      LocalDateTime fourthCourseDate = thirdCourseDate
              .plusDays(1)
              .minusHours(8);
      LocalDateTime fifthCourseDate = thirdCourseDate
              .plusDays(1)
              .plusHours(1);
      LocalDateTime sixthCourseDate = thirdCourseDate
              .plusDays(2)
              .minusHours(6);
      WeeklyCourses weeklyCourses = new WeeklyCourses();
      Course currentCourse = CourseBuilder.builder()
              .withCourseDate(thirdCourseDate)
              .withCourseName("Kurs-abc1")
              .withId(thirdCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("peter")
              .build();
      List<Course> courses = new ArrayList<>(List.of(CourseBuilder.builder()
              .withCourseDate(secondCourseDate)
              .withCourseName("Kurs-abc2")
              .withId(secondCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .withCourseInstructor("Heinz")
              .build(), currentCourse, CourseBuilder.builder()
              .withCourseDate(firstCourseDate)
              .withCourseName("Kurs-abc3")
              .withId(firstCourseId)
              .withCourseInstructor("Heinz")
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .build(), CourseBuilder.builder()
              .withCourseDate(fourthCourseDate)
              .withCourseName("Kurs-abc4")
              .withId(fourthCourseId)
              .withCourseInstructor("Heinz")
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .build(), CourseBuilder.builder()
              .withCourseDate(fifthCourseDate)
              .withCourseName("Kurs-5")
              .withId(fifthCourseId)
              .withCourseInstructor("Heinz")
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .build(), CourseBuilder.builder()
              .withCourseDate(sixthCourseDate)
              .withCourseName("Kurs-6")
              .withCourseInstructor("Heinz")
              .withId(sixthCourseId)
              .withCourseLocation(FITNESSPARK_HEUWAAGE)
              .build()));
      Collections.shuffle(courses);
      weeklyCourses.setCourses(courses);

      // When
      WeeklyCoursesDto weeklyCoursesDto = weeklyCoursesDtoMapper.mapToWeeklyCourseDto(weeklyCourses, currentCourse);

      // Then
      assertThat(weeklyCoursesDto.courseDtos().get(0).id(), is(firstCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(1).id(), is(secondCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).id(), is(thirdCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(3).id(), is(fourthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(4).id(), is(fifthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(5).id(), is(sixthCourseId));
      assertThat(weeklyCoursesDto.courseDtos().get(2).isCurrentCourse(), is(true));
   }
}