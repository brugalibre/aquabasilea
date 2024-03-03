package com.aquabasilea.domain.healthcheck.service;

import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.healthcheck.model.HealthCheckDo;
import com.aquabasilea.domain.healthcheck.model.HealthCheckResult;
import com.aquabasilea.domain.healthcheck.model.repository.HealthCheckRepository;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.aquabasilea.service.coursebooker.DryRunInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@link HealthCheckService} performs checks if the {@link AquabasileaCourseBooker} is still
 * compatible with the external course-booker api. The goal is to detect breaking changes in the rest-api of the external
 * course booker or failures in the authentication service (e.g. mismatch between local chrome-browser installation and version
 * determined by the webdriver
 */
public class HealthCheckService {

   public static final String COURSE_ID_MISSING = "Unable to retrieve course-id for health-check!";
   private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);
   private final AquabasileaCourseBookerService aquabasileaCourseBookerService;
   private final HealthCheckCourseEvaluator healthCheckCourseEvaluator;
   private final HealthCheckRepository healthCheckRepository;
   private final Supplier<String>  healthCheckUserIdEvaluator;

   public HealthCheckService(Supplier<String> healthCheckUserIdEvaluator, AquabasileaCourseBookerService aquabasileaCourseBookerService,
                             HealthCheckCourseEvaluator healthCheckCourseEvaluator, HealthCheckRepository healthCheckRepository) {
      this.healthCheckRepository = healthCheckRepository;
      this.healthCheckUserIdEvaluator = healthCheckUserIdEvaluator;
      this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
      this.healthCheckCourseEvaluator = healthCheckCourseEvaluator;
   }

   /**
    * Does the actual health-check of the course-booker
    *
    * @return a {@link HealthCheckResult} as a result of the health-check
    */
   public HealthCheckResult doHealthCheck() {
      LOG.info("Call healthcheck");
      HealthCheckResult healthCheckResult = healthCheckCourseEvaluator.getCourseId4HealthCheck()
              .map(doHealthCheckWithCourseId())
              .orElse(HealthCheckResult.failed(COURSE_ID_MISSING));
      LOG.info("Call healthcheck done. Result={}", healthCheckResult);
      return healthCheckResult;
   }

   /**
    * @return a previously created {@link HealthCheckDo}
    */
   public HealthCheckResult healthCheck() {
      HealthCheckDo healthCheckDo = healthCheckRepository.get();
      LOG.info("Healthcheck status checked: {}", healthCheckDo);
      return healthCheckDo.getHealthCheckResult();
   }

   public void doHealthCheckAndPersist() {
      HealthCheckResult healthCheckResult = doHealthCheck();
      HealthCheckDo healthCheckDo = healthCheckRepository.get();
      if (healthCheckResult.status()) {
         healthCheckDo.setLastSuccessful(LocalDateTime.now());
         healthCheckDo.setLastFailed(null);
      } else {
         healthCheckDo.setLastFailed(LocalDateTime.now());
      }
      LOG.info("Persisting HealthCheckDo {}", healthCheckDo);
      healthCheckRepository.save(healthCheckDo);
   }

   private Function<String, HealthCheckResult> doHealthCheckWithCourseId() {
      return courseId -> {
         String userId = healthCheckUserIdEvaluator.get();
         CourseBookingResultDetails courseBookingResultDetails = aquabasileaCourseBookerService.bookCourseDryRun(new DryRunInfo(userId, courseId, false));
         if (courseBookingResultDetails.getCourseBookResult() == CourseBookResult.DRY_RUN_SUCCESSFUL) {
            return HealthCheckResult.successful();
         }
         return HealthCheckResult.failed(courseBookingResultDetails.getErrorMessage());
      };
   }
}
