package com.aquabasilea.application.exception;

import com.brugalibre.notification.config.AlertSendConfigProvider;
import com.brugalibre.notification.send.common.service.ApplicationErrorAlertSender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
   private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
   private static final String LOG_ERROR_MSG = "Exception in Thread %s. Application is going to be shutdown";
   private static final String APPLICATION_FAILURE_MSG = "Aquabasilea-Kurs-Bucher: Es ist ein schwerwiegender Fehler aufgetreten, die Applikation wird heruntergefahren!\n\nFehler details: '%s'";
   private static final String APPLICATION_FAILURE_TITLE = "Fehler im Aquabasilea-Kurs-Bucher!";
   private final AlertSendConfigProvider alertSendConfigProvider;

   @Autowired
   public GlobalExceptionHandler(AlertSendConfigProvider alertSendConfigProvider) {
      this.alertSendConfigProvider = alertSendConfigProvider;
   }

   @PostConstruct
   public void init(){
      Thread.setDefaultUncaughtExceptionHandler(this);
   }

   @Override
   public void uncaughtException(Thread t, Throwable e) {
      LOG.error(LOG_ERROR_MSG.formatted(t), e);
      ApplicationErrorAlertSender applicationErrorAlertSender = ApplicationErrorAlertSender.of(alertSendConfigProvider);
      applicationErrorAlertSender.sendApplicationErrorMessage(APPLICATION_FAILURE_TITLE, APPLICATION_FAILURE_MSG.formatted(e.getMessage()));
      System.exit(1);
   }
}
