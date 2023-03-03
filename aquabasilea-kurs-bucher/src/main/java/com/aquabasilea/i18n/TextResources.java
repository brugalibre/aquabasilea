package com.aquabasilea.i18n;

public class TextResources {

   private TextResources(){
      // private
   }

   // Kurs
   public static final String COURSE_REPRESENTATION = "%s, Kursleiter: %s, %s %s um %s Uhr (Kursort: %s)";

   public static final String PAUSE_APP = "Pausiere Kurs-Bucher";
   public static final String RESUME_APP = "Reaktiviere Kurs-Bucher";
   public static final String TOOLTIP_COURSE_HAS_NO_COURSE_DEF = "Achtung! Dieser Kurs kann nicht gebucht werden.\nDer Kurs existiert im offiziellen Migros-Kursprogramm nicht";
   public static final String TOOLTIP_COURSE_IS_PAUSED = "Dieser Kurs ist pausiert, bis der nächste aktive Kurs gebucht wurde";
   public static final String TOOLTIP_COURSE_IS_CURRENT_COURSE = "Dieser Kurs wird als nächstes gebucht. Kursinstruktor:in ist %s";

   // Statistics
   public static final String UPTIME_YEARS_AND_MONTH = "%s Jahre, %s Monate";
   public static final String UPTIME_DAYS_HOURS_MINUTES= "%s Tage, %s Stunden, %s Minuten";
   public static final String NEXT_LAST_AQUABASILEA_COURSE_DEF_UPDATE= "%s Uhr";

   public static final String INFO_TEXT_INIT = "Ermittlung des nächsten Kurses läuft..";
   public static final String INFO_TEXT_IDLE_BEFORE_DRY_RUN = "Kurs '%s' am %s. Testlauf startet am %s.";
   public static final String INFO_TEXT_IDLE_BEFORE_BOOKING = "Kurs '%s' am %s. Buchung startet am %s.";
   public static final String INFO_TEXT_APP_PAUSED = "Applikation pausiert";
   public static final String INFO_TEXT_BOOKING_COURSE = "Buchung von Kurs '%s' läuft";
   public static final String INFO_TEXT_BOOKING_COURSE_DRY_RUN = "Buchung von Kurs '%s' läuft (Testlauf)";

   // Booking
   public static final String COURSE_SUCCESSFULLY_BOOKED = "Kurs '%s' erfolgreich gebucht";
   public static final String COURSE_NOT_BOOKABLE = "Kurs '%s' konnte nicht gebucht werden!";
   public static final String COURSE_NOT_BOOKABLE_NO_SINGLE_RESULT = "Kurs '%s' konnte nicht gebucht werden, da nicht eindeutig gefiltert werden konnte!";
   public static final String
           COURSE_NOT_BOOKABLE_EXCEPTION = "Kurs '%s' konnte nicht gebucht werden, da ein interner Fehler aufgetreten ist ('%s')";
   public static final String COURSE_BOOKING_SKIPPED_COURSE_NO_COURSE_DEF = "Kurs '%s' wurde nicht gebucht! Der Kurs existiert im offiziellen Migros-Kursprogramm nicht";
   public static final String COURSE_DRY_RUN_SKIPPED_COURSE_NO_COURSE_DEF = "Testlauf für Kurs '%s' abgebrochen! Der Kurs existiert im offiziellen Migros-Kursprogramm nicht";
   public static final String DRY_RUN_FINISHED_SUCCESSFULLY = "Testlauf für Kurs '%s' erfolgreich!";
   public static final String DRY_RUN_FINISHED_FAILED = "Testlauf für Kurs '%s' fehlgeschlagen!";

   // Login
   public static final String MIGROS_FITNESS_CREDENTIALS_NOT_VALID = "Es gibt kein Migros-Fitness Login mit dem Benutzername '%s' oder das Passwort ist falsch";

   // Errors
   public static final String ERROR_COURSE_ALREADY_EXISTS = "Kurs %s existiert bereits und kann nicht erneut hinzugefügt werden";
}
