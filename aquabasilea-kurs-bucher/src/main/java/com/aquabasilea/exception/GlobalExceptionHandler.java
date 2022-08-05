package com.aquabasilea.exception;

import com.aquabasilea.alerting.send.BasicAlertSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
   private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
   private static final String LOG_ERROR_MSG = "Exception in Thread {}. Application is going to be shutdown";
   private static final String APPLICATION_FAILURE_MSG = "Aquabasilea-Kurs-Bucher: Es ist ein schwerwiegender Fehler aufgetreten!\n" +
           "Fehler details: '%s'\n" +
           "Applikation wird heruntergefahren";

   @Override
   public void uncaughtException(Thread t, Throwable e) {
      LOG.error(LOG_ERROR_MSG.formatted(t), e);
      BasicAlertSender basicAlertSender = new BasicAlertSender();
      basicAlertSender.sendMessage(APPLICATION_FAILURE_MSG.formatted(e.getMessage()));
      System.exit(-1);
   }
}
