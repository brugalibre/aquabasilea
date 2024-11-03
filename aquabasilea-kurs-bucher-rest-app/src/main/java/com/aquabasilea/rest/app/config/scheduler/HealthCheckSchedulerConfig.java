package com.aquabasilea.rest.app.config.scheduler;

import com.aquabasilea.domain.healthcheck.service.HealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import static com.aquabasilea.application.config.HealthCheckConfig.HEALTHCHECK_TECH_USER_NAME;

@Configuration
@EnableScheduling
public class HealthCheckSchedulerConfig {
   private static final Logger LOG = LoggerFactory.getLogger(HealthCheckSchedulerConfig.class);
   private final HealthCheckService healthCheckService;
   private final boolean scheduleEnabled;

   public HealthCheckSchedulerConfig(HealthCheckService healthCheckService, @Value(HEALTHCHECK_TECH_USER_NAME) String techUserName) {
      this.healthCheckService = healthCheckService;
      this.scheduleEnabled = StringUtils.hasText(techUserName);
   }

   @EventListener
   public void onApplicationEvent(ApplicationStartedEvent event /*unused*/) {
      if (scheduleEnabled) {
         LOG.info("Do health-check persist results on start-up");
         healthCheckService.doHealthCheckAndPersist();
      } else {
         LOG.info("Health-check disabled");
      }
   }

   @Scheduled(cron = "0 0 1,5,13 * * *")
   public void scheduleTaskUsingCronExpression() {
      if (scheduleEnabled) {
         LOG.info("Do health-check and persist results");
         healthCheckService.doHealthCheckAndPersist();
      } else {
         LOG.info("Health-check disabled");
      }
   }
}

