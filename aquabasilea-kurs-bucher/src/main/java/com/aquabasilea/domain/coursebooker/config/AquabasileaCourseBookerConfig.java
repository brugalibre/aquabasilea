package com.aquabasilea.domain.coursebooker.config;

import com.aquabasilea.domain.coursebooker.booking.facade.AquabasileaCourseBookerType;
import com.aquabasilea.domain.coursedef.update.facade.CourseDefExtractorType;
import com.brugalibre.util.config.yml.YmlConfig;
import com.brugalibre.util.file.yml.YamlService;

import java.time.Duration;

import static java.util.Objects.nonNull;

public class AquabasileaCourseBookerConfig implements YmlConfig {

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
   private static final YamlService YAML_SERVICE = new YamlService();
   private Integer secondsToStartBookerEarlier;
   private Integer minutesToStartDryRunEarlier;
   private Duration durationToStartDryRunEarlier;
   private Duration durationToStartBookerEarlier;
   private String courseConfigFile;

   private Duration maxBookerStartDelay;
   private int daysToBookCourseEarlier;
   private CourseDefExtractorType courseDefExtractorType;
   private AquabasileaCourseBookerType aquabasileaCourseBookerType;

   public AquabasileaCourseBookerConfig() {
      init(AQUABASILEA_COURSE_BOOKER_CONFIG_FILE);
   }

   public AquabasileaCourseBookerConfig(String configFile) {
      init(configFile);
   }

   @Override
   public void setConfigFile(String configFile) {
      this.courseConfigFile = configFile;
   }

   public void setDaysToBookCourseEarlier(int daysToBookCourseEarlier) {
      this.daysToBookCourseEarlier = daysToBookCourseEarlier;
   }

   public void setSecondsToStartBookerEarlier(Integer secondsToStartBookerEarlier) {
      this.secondsToStartBookerEarlier = secondsToStartBookerEarlier;
   }

   public void setMinutesToStartDryRunEarlier(Integer minutesToStartDryRunEarlier) {
      this.minutesToStartDryRunEarlier = minutesToStartDryRunEarlier;
   }

   void setDurationToStartBookerEarlier(Duration durationToStartBookerEarlier) {
      this.durationToStartBookerEarlier = durationToStartBookerEarlier;
   }

   void setDurationToStartDryRunEarlier(Duration durationToStartDryRunEarlier) {
      this.durationToStartDryRunEarlier = durationToStartDryRunEarlier;
   }

   private void init(String configFile) {
      setMaxBookerStartDelay(Duration.ofSeconds(30));
      this.courseConfigFile = configFile;
      this.durationToStartBookerEarlier = DURATION_TO_START_BOOKER_EARLIER;
      this.durationToStartDryRunEarlier = DURATION_TO_START_DRY_RUN_EARLIER;
      this.daysToBookCourseEarlier = DAYS_TO_BOOK_COURSE_EARLIER;
      this.courseDefExtractorType = CourseDefExtractorType.AQUABASILEA_WEB;
      this.aquabasileaCourseBookerType = AquabasileaCourseBookerType.AQUABASILEA_WEB;
   }

   /**
    * Refreshes the configurable values from the {@link #AQUABASILEA_COURSE_BOOKER_CONFIG_FILE}
    */
   @Override
   public AquabasileaCourseBookerConfig refresh() {
      readConfigFromFile();
      return this;
   }

   private void readConfigFromFile() {
      AquabasileaCourseBookerConfig externalReadConfig = YAML_SERVICE.readYamlIgnoreMissingFile(courseConfigFile, getClass());
      if (nonNull(externalReadConfig.getCourseDefExtractorType())) {
         this.courseDefExtractorType = externalReadConfig.courseDefExtractorType;
      }
      if (nonNull(externalReadConfig.getAquabasileaCourseBookerType())) {
         this.aquabasileaCourseBookerType = externalReadConfig.aquabasileaCourseBookerType;
      }
      if (nonNull(externalReadConfig.secondsToStartBookerEarlier)) {
         this.durationToStartBookerEarlier = Duration.ofSeconds(externalReadConfig.secondsToStartBookerEarlier);
      }
      if (nonNull(externalReadConfig.minutesToStartDryRunEarlier)) {
         this.durationToStartDryRunEarlier = Duration.ofMinutes(externalReadConfig.minutesToStartDryRunEarlier);
      }
   }

   public CourseDefExtractorType getCourseDefExtractorType() {
      return courseDefExtractorType;
   }

   public void setCourseDefExtractorType(CourseDefExtractorType courseDefExtractorType) {
      this.courseDefExtractorType = courseDefExtractorType;
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

   public void setSecondsToStartBookerEarlier(int secondsToStartBookerEarlier) {
      this.secondsToStartBookerEarlier = secondsToStartBookerEarlier;
   }

   public AquabasileaCourseBookerType getAquabasileaCourseBookerType() {
      return aquabasileaCourseBookerType;
   }

   public void setAquabasileaCourseBookerType(AquabasileaCourseBookerType aquabasileaCourseBookerType) {
      this.aquabasileaCourseBookerType = aquabasileaCourseBookerType;
   }

   public void setMaxBookerStartDelay(Duration maxBookerStartDelay) {
      this.maxBookerStartDelay = maxBookerStartDelay;
   }

   public Duration getMaxBookerStartDelay() {
      return maxBookerStartDelay;
   }

   @Override
   public String toString() {
      return "AquabasileaCourseBookerConfig{" +
              "secondsToStartBookerEarlier=" + secondsToStartBookerEarlier +
              ", minutesToStartDryRunEarlier=" + minutesToStartDryRunEarlier +
              ", durationToStartDryRunEarlier=" + durationToStartDryRunEarlier +
              ", maxBookerStartDelay=" + maxBookerStartDelay +
              ", durationToStartBookerEarlier=" + durationToStartBookerEarlier +
              ", courseConfigFile='" + courseConfigFile + '\'' +
              ", daysToBookCourseEarlier=" + daysToBookCourseEarlier +
              ", courseDefExtractorType=" + courseDefExtractorType +
              ", aquabasileaCourseBookerType=" + aquabasileaCourseBookerType +
              '}';
   }
}
