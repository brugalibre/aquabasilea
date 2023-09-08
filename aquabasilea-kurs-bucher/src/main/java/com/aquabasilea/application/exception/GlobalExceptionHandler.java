package com.aquabasilea.application.exception;

import com.aquabasilea.notification.alertsend.config.AlertSendConfigProviderImpl;
import com.brugalibre.notification.api.v1.alerttype.AlertType;
import com.brugalibre.notification.send.common.service.ApplicationErrorAlertSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
   private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
   private static final String LOG_ERROR_MSG = "Exception in Thread %s. Application is going to be shutdown";
   private static final String APPLICATION_FAILURE_MSG = "Aquabasilea-Kurs-Bucher: Es ist ein schwerwiegender Fehler aufgetreten, die Applikation wird heruntergefahren!\n\nFehler details: '%s'";
   private static final String APPLICATION_FAILURE_TITLE = "Fehler im Aquabasilea-Kurs-Bucher!";

   @Override
   public void uncaughtException(Thread t, Throwable e) {
      LOG.error(LOG_ERROR_MSG.formatted(t), e);
      ApplicationErrorAlertSender applicationErrorAlertSender = ApplicationErrorAlertSender.of(AlertSendConfigProviderImpl.of(() -> List.of(AlertType.SMS)));
      applicationErrorAlertSender.sendApplicationErrorMessage(APPLICATION_FAILURE_TITLE, APPLICATION_FAILURE_MSG.formatted(e.getMessage()));
      System.exit(1);
   }
}
