package com.aquabasilea.coursebooker.config;

import com.aquabasilea.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.util.Objects.nonNull;

public class AquabasileaCourseBookerConfig {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerConfig.class);
   /**
    * Offset in minutes when the booker starts earlier
    * We have to start the booker slightly earlier, so that we are ready to book, right when the course becomes bookable
    */
   public static final Duration DURATION_TO_START_BOOKER_EARLIER = Duration.ofSeconds(90);

   /**
    * Offset in minutes when the dry run starts earlier
    */
   public static final Duration DURATION_TO_START_DRY_RUN_EARLIER = Duration.ofMinutes(120);

   /**
    * A course gets bookable 24h prior to the actual date it takes place.
    * That's why we have to start the booker at least 24h earlier to book a course for the next day
    */
   private static final int DAYS_TO_BOOK_COURSE_EARLIER = 1;

   private static final String AQUABASILEA_COURSE_BOOKER_CONFIG_FILE = "config/aquabasilea-kurs-bucher-config.yml";
   private Duration durationToStartDryRunEarlier;
   private Duration durationToStartBookerEarlier;
   private String courseConfigFile;
   private int daysToBookCourseEarlier;

   @Override
   public String toString() {
      return "AquabasileaCourseBookerConfig{" +
              "durationToStartDryRunEarlier=" + durationToStartDryRunEarlier +
              ", durationToStartBookerEarlier=" + durationToStartBookerEarlier +
              ", courseConfigFile='" + courseConfigFile + '\'' +
              ", daysToBookCourseEarlier=" + daysToBookCourseEarlier +
              '}';
   }

   public AquabasileaCourseBookerConfig() {
      init(AQUABASILEA_COURSE_BOOKER_CONFIG_FILE);
   }

   public AquabasileaCourseBookerConfig(String configFile) {
      init(configFile);
   }

   public void setDaysToBookCourseEarlier(int daysToBookCourseEarlier) {
      this.daysToBookCourseEarlier = daysToBookCourseEarlier;
   }

   void setDurationToStartBookerEarlier(Duration durationToStartBookerEarlier) {
      this.durationToStartBookerEarlier = durationToStartBookerEarlier;
   }

   void setDurationToStartDryRunEarlier(Duration durationToStartDryRunEarlier) {
      this.durationToStartDryRunEarlier = durationToStartDryRunEarlier;
   }

   private void init(String configFile) {
      this.courseConfigFile = configFile;
      this.durationToStartBookerEarlier = DURATION_TO_START_BOOKER_EARLIER;
      this.durationToStartDryRunEarlier = DURATION_TO_START_DRY_RUN_EARLIER;
      this.daysToBookCourseEarlier = DAYS_TO_BOOK_COURSE_EARLIER;
      readConfigFromFile();
   }

   private void readConfigFromFile() {
      AquabasileaCourseBookerConfigImport configImport = YamlUtil.readYamlIgnoreMissingFile(this.courseConfigFile, AquabasileaCourseBookerConfigImport.class);
      if (nonNull(configImport.getMinutesToStartDryRunEarlier())) {
         this.durationToStartDryRunEarlier = Duration.ofMinutes(configImport.getMinutesToStartDryRunEarlier());
         LOG.info("Override default value for 'durationToStartDryRunEarlier' with customized value {}", durationToStartDryRunEarlier);
      }
   }

   /**
    * Refreshes all config values of this {@link AquabasileaCourseBookerConfig}
    * Note: The location of the configuration file must not change!
    */
   public void refresh() {
      LOG.info("Refresh configuration..");
      readConfigFromFile();
      LOG.info("Configuration refreshed: {}", this);
   }

   /**
    * The {@link Duration} when the booker starts earlier
    * We have to start the booker slightly earlier, so that we are ready to book, right when the course becomes bookable
    *
    * @return The {@link Duration} when the booker starts earlier
    */
   public Duration getDurationToStartBookerEarlier() {
      return durationToStartBookerEarlier;
   }

   /**
    * @return The {@link Duration} when the dry run starts earlier
    */
   public Duration getDurationToStartDryRunEarlier() {
      return durationToStartDryRunEarlier;
   }

   public int getDaysToBookCourseEarlier() {
      return daysToBookCourseEarlier;
   }
}
