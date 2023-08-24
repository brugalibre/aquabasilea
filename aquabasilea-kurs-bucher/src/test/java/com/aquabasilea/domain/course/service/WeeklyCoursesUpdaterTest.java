package com.aquabasilea.domain.course.service;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aquabasilea.domain.course.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

class WeeklyCoursesUpdaterTest {

    private static final String USER_ID = "34534";

    @Test
    void testEvaluateNextCourseAndStateAndUpdateCourseWithoutCourseDef() {

        // Given
        // The course takes place tomorrow, and we are more than 24h earlier
        LocalDateTime courseDate = LocalDateTime.now().plusDays(2);
        String courseId = UUID.randomUUID().toString();
        String courseName = "Kurs-51";
        TestCaseBuilder tcb = new TestCaseBuilder()
                .withWeeklyCourses(new WeeklyCourses(USER_ID, List.of(Course.CourseBuilder.builder()
                        .withCourseDate(courseDate)
                        .withCourseName(courseName)
                        .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                        .withId(courseId)
                        .withHasCourseDef(false)
                        .build())))
                .withCourseDef(new CourseDef("id", USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, courseName, "peter"))
                .build();

        // When
        tcb.weeklyCoursesUpdater.updateCoursesHasCourseDef(tcb.weeklyCourses);

        // Then
        assertThat(tcb.weeklyCourses.getCourses().get(0).getHasCourseDef(), is(true));

    }

    private static class TestCaseBuilder {

        private final WeeklyCoursesRepository weeklyCoursesRepository;
        private final List<CourseDef> courseDefs;
        private final CourseDefRepository courseDefRepository;
        private WeeklyCourses weeklyCourses;
        private WeeklyCoursesUpdater weeklyCoursesUpdater;

        private TestCaseBuilder() {
            this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
            this.courseDefRepository = mock(CourseDefRepository.class);
            this.courseDefs = new ArrayList<>();
        }

        private TestCaseBuilder withWeeklyCourses(WeeklyCourses weeklyCourses) {
            this.weeklyCourses = weeklyCourses;
            return this;
        }

        private TestCaseBuilder build() {
            Mockito.when(weeklyCoursesRepository.getByUserId(USER_ID)).thenReturn(weeklyCourses);
            Mockito.when(courseDefRepository.getAllByUserId(USER_ID)).thenReturn(courseDefs);
            this.weeklyCoursesUpdater = new WeeklyCoursesUpdater(weeklyCoursesRepository, courseDefRepository);
            return this;
        }

        public TestCaseBuilder withCourseDef(CourseDef courseDef) {
            this.courseDefs.add(courseDef);
            return this;
        }
    }
}