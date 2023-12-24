package com.aquabasilea.domain.course.service;

import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class WeeklyCoursesUpdaterTest {

    private static final String USER_ID = "34534";

    @Autowired
    private WeeklyCoursesRepository weeklyCoursesRepository;

    @Autowired
    private CourseDefRepository courseDefRepository;

    @Autowired
    private CourseLocationRepository courseLocationRepository;


    @BeforeEach
    public void setUp() {
        courseLocationRepository.save(MIGROS_FITNESSCENTER_AQUABASILEA);
    }

    @AfterEach
    public void cleanUp() {
        this.weeklyCoursesRepository.deleteAll();
        this.courseDefRepository.deleteAll();
        this.courseLocationRepository.deleteAll();
    }

    @Test
    void testEvaluateNextCourseAndStateAndUpdateCourseWithoutCourseDef() {

        // Given
        CourseLocation courseLocation = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
        // The course takes place tomorrow, and we are more than 24h earlier
        LocalDateTime courseDate = LocalDateTime.now().plusDays(2);
        String courseName = "Kurs-51";
        TestCaseBuilder tcb = new TestCaseBuilder()
                .withWeeklyCourses(new WeeklyCourses(USER_ID, List.of(CourseBuilder.builder()
                        .withCourseDate(courseDate)
                        .withCourseName(courseName)
                        .withCourseLocation(courseLocation)
                        .withCourseInstructor("Peter")
                        .withHasCourseDef(false)
                        .build())))
                .withCourseDef(new CourseDef("id", USER_ID, courseDate, courseLocation, courseName, "peter"))
                .build();

        // When
        tcb.weeklyCoursesUpdater.updateCoursesHasCourseDef(tcb.weeklyCourses);

        // Then
        assertThat(tcb.weeklyCourses.getCourses().get(0).getHasCourseDef(), is(true));
    }

    private class TestCaseBuilder {

        private final List<CourseDef> courseDefs;
        private WeeklyCourses weeklyCourses;
        private WeeklyCoursesUpdater weeklyCoursesUpdater;

        private TestCaseBuilder() {
            this.courseDefs = new ArrayList<>();
        }

        private TestCaseBuilder withWeeklyCourses(WeeklyCourses weeklyCourses) {
            this.weeklyCourses = weeklyCourses;
            return this;
        }

        private TestCaseBuilder build() {
            courseDefRepository.saveAll(courseDefs);
            weeklyCourses = weeklyCoursesRepository.save(weeklyCourses);
            this.weeklyCoursesUpdater = new WeeklyCoursesUpdater(weeklyCoursesRepository, courseDefRepository);
            return this;
        }

        public TestCaseBuilder withCourseDef(CourseDef courseDef) {
            this.courseDefs.add(courseDef);
            return this;
        }
    }
}