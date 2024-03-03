package com.aquabasilea.domain.healthcheck.service;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.healthcheck.model.HealthCheckResult;
import com.aquabasilea.domain.healthcheck.model.repository.HealthCheckRepository;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.aquabasilea.service.coursebooker.DryRunInfo;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.aquabasilea.domain.healthcheck.service.HealthCheckService.COURSE_ID_MISSING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthCheckServiceTest {

    public static final String COURSE_NAME = "courseName";

    @Test
    void healthCheckSuccessful() {
        // Given
        String courseId = "123";
        String userId = "userid";
        CourseBookingResultDetailsImpl courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_SUCCESSFUL, COURSE_NAME, null);
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mockAquabasileaCourseBookerService(userId, courseId, courseBookingResultDetails);
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mockHealthCheckCourseEvaluator(courseId);
        HealthCheckService healthCheckService = new HealthCheckService(() -> userId, aquabasileaCourseBookerService, healthCheckCourseEvaluator, mock(HealthCheckRepository.class));

        // When
        HealthCheckResult actualHealthCheckResult = healthCheckService.doHealthCheck();

        // Then
        assertThat(actualHealthCheckResult.status()).isTrue();
    }

    @Test
    void healthCheckFailed() {
        // Given
        String courseId = "123";
        String userId = "userid";
        String errorMessage = "error!";
        CourseBookingResultDetailsImpl courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_FAILED, COURSE_NAME, errorMessage);
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mockAquabasileaCourseBookerService(userId, courseId, courseBookingResultDetails);
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mockHealthCheckCourseEvaluator(courseId);
        HealthCheckService healthCheckService = new HealthCheckService(() -> userId, aquabasileaCourseBookerService, healthCheckCourseEvaluator, mock(HealthCheckRepository.class));

        // When
        HealthCheckResult actualHealthCheckResult = healthCheckService.doHealthCheck();

        // Then
        assertThat(actualHealthCheckResult.status()).isFalse();
        assertThat(actualHealthCheckResult.message()).isEqualTo(errorMessage);
    }

    @Test
    void healthCheckCourseIdEvaluationFailed() {
        // Given
        String userId = "userid";
        CourseBookingResultDetailsImpl courseBookingResultDetails = CourseBookingResultDetailsImpl.of(CourseBookResult.DRY_RUN_SUCCESSFUL, COURSE_NAME, null);
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mockAquabasileaCourseBookerService(userId, null, courseBookingResultDetails);
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mockHealthCheckCourseEvaluator(null);
        HealthCheckService healthCheckService = new HealthCheckService(() -> userId, aquabasileaCourseBookerService, healthCheckCourseEvaluator, mock(HealthCheckRepository.class));

        // When
        HealthCheckResult actualHealthCheckResult = healthCheckService.doHealthCheck();

        // Then
        assertThat(actualHealthCheckResult.status()).isFalse();
        assertThat(actualHealthCheckResult.message()).isEqualTo(COURSE_ID_MISSING);
    }

    private static HealthCheckCourseEvaluator mockHealthCheckCourseEvaluator(String courseId) {
        HealthCheckCourseEvaluator healthCheckCourseEvaluator = mock(HealthCheckCourseEvaluator.class);
        when(healthCheckCourseEvaluator.getCourseId4HealthCheck()).thenReturn(Optional.ofNullable(courseId));
        return healthCheckCourseEvaluator;
    }

    private static AquabasileaCourseBookerService mockAquabasileaCourseBookerService(String userId, String courseId, CourseBookingResultDetailsImpl courseBookingResultDetails) {
        AquabasileaCourseBookerService aquabasileaCourseBookerService = mock(AquabasileaCourseBookerService.class);
        when(aquabasileaCourseBookerService.bookCourseDryRun(eq(new DryRunInfo(userId, courseId, false)))).thenReturn(courseBookingResultDetails);
        return aquabasileaCourseBookerService;
    }
}