package com.aquabasilea.domain.healthcheck.service;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.healthcheck.model.HealthCheckDo;
import com.aquabasilea.domain.healthcheck.model.HealthCheckResult;
import com.aquabasilea.domain.healthcheck.model.repository.HealthCheckRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.aquabasilea.service.coursebooker.DryRunInfo;
import com.brugalibre.util.date.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class HealthCheckServiceIntegrationTest {

    public static final String COURSE_NAME = "courseName";

    @Autowired
    private HealthCheckRepository healthCheckRepository;

    @BeforeEach
    public void setUp() {
        tearDown();
        healthCheckRepository.save(new HealthCheckDo());
    }

    @AfterEach
    public void tearDown() {
        healthCheckRepository.deleteAll();
    }

    @Test
    void healthCheckSuccessful() {
        // Given
        String courseId = "123";
        String userId = "userid";
        CourseBookingResultDetailsImpl courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_SUCCESSFUL, COURSE_NAME, null);
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mockAquabasileaCourseBookerService(userId, courseId, courseBookingResultDetails);
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mockHealthCheckCourseEvaluator(courseId);
        HealthCheckService healthCheckService = new HealthCheckService(() -> userId, aquabasileaCourseBookerService, healthCheckCourseEvaluator, healthCheckRepository);

        // When
        healthCheckService.doHealthCheckAndPersist();
        HealthCheckDo healthCheckDo = healthCheckRepository.get();

        // Then
        assertThat(healthCheckDo.getLastFailed()).isNull();
        assertThat(healthCheckDo.getLastSuccessful()).isNotNull();
        assertThat(healthCheckDo.getHealthCheckResult()).isEqualTo(HealthCheckResult.successful());
    }

    @Test
    void healthCheckSuccessfulAndThanFailed() {
        // Given
        String courseId = "123";
        String userId = "userid";
        String errorMsg = "upsidupsi";
        LocalDateTime now = LocalDateTime.now();
        String expectedErrorMsg = HealthCheckDo.HEALTH_CHECK_FAILED_MSG.formatted(DateUtil.toString(now, Locale.getDefault()));
        CourseBookingResultDetailsImpl courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_SUCCESSFUL, COURSE_NAME, null);
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mockAquabasileaCourseBookerService(userId, courseId, courseBookingResultDetails);
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mockHealthCheckCourseEvaluator(courseId);
        HealthCheckService healthCheckService = new HealthCheckService(() -> userId, aquabasileaCourseBookerService, healthCheckCourseEvaluator, healthCheckRepository);

        // When
        healthCheckService.doHealthCheckAndPersist();
        HealthCheckDo healthCheckDoFirstTime = healthCheckRepository.get();

        when(aquabasileaCourseBookerService.bookCourseDryRun(eq(new DryRunInfo(userId, courseId, false)))).thenReturn(CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_COURSE_FULLY_BOOKED, COURSE_NAME, errorMsg));
        healthCheckService.doHealthCheckAndPersist();
        HealthCheckDo healthCheckDoSecondTime = healthCheckRepository.get();

        // Then
        assertThat(healthCheckDoFirstTime.getLastFailed()).isNull();
        assertThat(healthCheckDoFirstTime.getLastSuccessful()).isNotNull();
        assertThat(healthCheckDoFirstTime.getHealthCheckResult()).isEqualTo(HealthCheckResult.successful());
        assertThat(healthCheckDoSecondTime.getLastSuccessful()).isNotNull();
        assertThat(healthCheckDoSecondTime.getHealthCheckResult()).isNotNull();
        assertThat(healthCheckDoSecondTime.getHealthCheckResult()).isEqualTo(HealthCheckResult.failed(expectedErrorMsg));
    }

    private static HealthCheckCourseEvaluator mockHealthCheckCourseEvaluator(String courseId) {
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mock(HealthCheckCourseEvaluator.class);
        when(healthCheckCourseEvaluator.getCourseId4HealthCheck()).thenReturn(Optional.of(courseId));
        return healthCheckCourseEvaluator;
    }

    private static AquabasileaCourseBookerService mockAquabasileaCourseBookerService(String userId, String courseId, CourseBookingResultDetailsImpl courseBookingResultDetails) {
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mock(AquabasileaCourseBookerService.class);
        when(aquabasileaCourseBookerService.bookCourseDryRun(eq(new DryRunInfo(userId, courseId, false)))).thenReturn(courseBookingResultDetails);
        return aquabasileaCourseBookerService;
    }
}