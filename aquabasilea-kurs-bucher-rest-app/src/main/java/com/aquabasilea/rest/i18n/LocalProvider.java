package com.aquabasilea.rest.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * The {@link LocalProvider} provides the current users {@link Locale}
 * In fact, we always return {@link Locale#GERMAN}
 */
@Component
public class LocalProvider {

   private static LocalProvider localProvider;

   @Autowired
   private void setLocalProvider(LocalProvider localProvider) {
      LocalProvider.localProvider = localProvider;
   }

   public static LocalProvider getInstance() {
      return localProvider;
   }

   public Locale getCurrentLocale() {
      return Locale.GERMAN;
   }
}
