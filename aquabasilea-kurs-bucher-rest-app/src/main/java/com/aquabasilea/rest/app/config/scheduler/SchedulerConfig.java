package com.aquabasilea.rest.app.config.scheduler;

import com.aquabasilea.domain.healthcheck.service.HealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

   private static final Logger LOG = LoggerFactory.getLogger(SchedulerConfig.class);
   private final HealthCheckService healthCheckService;

   public SchedulerConfig(HealthCheckService healthCheckService) {
      this.healthCheckService = healthCheckService;
   }


   @EventListener
   public void onApplicationEvent(ApplicationStartedEvent event /*unused*/) {
      LOG.info("Do health-check persist results on start-up");
      healthCheckService.doHealthCheckAndPersist();
   }

   @Scheduled(cron = "0 0 1,5,13 * * *")
   public void scheduleTaskUsingCronExpression() {
      LOG.info("Do health-check and persist results");
      healthCheckService.doHealthCheckAndPersist();
   }
}

