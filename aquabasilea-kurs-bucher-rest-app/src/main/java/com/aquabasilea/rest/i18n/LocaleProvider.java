package com.aquabasilea.rest.i18n;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * The {@link LocaleProvider} provides the current users {@link Locale}
 * In fact, we always return {@link Locale#GERMAN}
 */
@Component
public class LocaleProvider {

   public Locale getCurrentLocale() {
      return Locale.GERMAN;
   }

   public static Locale getDefaultLocale () {
      return Locale.GERMAN;
   }
}
