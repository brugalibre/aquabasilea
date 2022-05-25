package com.aquabasilea.rest.i18n;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * The {@link LocalProvider} provides the current users {@link Locale}
 * In fact, we always return {@link Locale#GERMAN}
 */
@Component
public class LocalProvider {
   public Locale getCurrentLocale() {
      return Locale.GERMAN;
   }
}
