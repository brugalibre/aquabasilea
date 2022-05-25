package com.aquabasilea.course.aquabasilea.update;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.mapping.CoursesDefEntityMapper;
import com.aquabasilea.course.aquabasilea.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfigImport;
import com.aquabasilea.persistence.entity.statistic.StatisticsHelper;
import com.aquabasilea.util.YamlUtil;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CourseDefUpdater {

   private static final String AQUABASILEA_COURSE_BOOKER_CONFIG_FILE = "config/aquabasilea-kurs-bucher-config.yml";
   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdater.class);
   private final Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier;
   private final CourseDefRepository courseDefRepository;
   private final CoursesDefEntityMapper coursesDefEntityMapper;
   private final ExecutorService executorService;
   private final CourseDefUpdaterScheduler courseDefUpdaterScheduler;
   private final StatisticsHelper statisticsHelper;
   private final String configFile;
   private final List<CourseDefUpdatedNotifier> courseDefUpdatedNotifiers;

   private boolean isCourseDefUpdateRunning;

   public CourseDefUpdater(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier, StatisticsHelper statisticsHelper, CourseDefRepository courseDefRepository, CoursesDefEntityMapper coursesDefEntityMapper) {
      this(aquabasileaCourseExtractorSupplier, statisticsHelper, courseDefRepository, coursesDefEntityMapper, AQUABASILEA_COURSE_BOOKER_CONFIG_FILE, new CourseDefUpdateDate());
   }

   public CourseDefUpdater(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier, StatisticsHelper statisticsHelper,
                           CourseDefRepository courseDefRepository, CoursesDefEntityMapper coursesDefEntityMapper, String configFile, CourseDefUpdateDate courseDefUpdateDate) {
      this.aquabasileaCourseExtractorSupplier = aquabasileaCourseExtractorSupplier;
      this.coursesDefEntityMapper = coursesDefEntityMapper;
      this.courseDefRepository = courseDefRepository;
      this.statisticsHelper = statisticsHelper;
      this.executorService = Executors.newSingleThreadExecutor();
      this.configFile = configFile;
      AquabasileaCourseBookerConfigImport configImport = YamlUtil.readYamlIgnoreMissingFile(configFile, AquabasileaCourseBookerConfigImport.class);
      this.courseDefUpdaterScheduler = new CourseDefUpdaterScheduler(this::updateCourseDefsAsRunnable, configImport.getDefaultCourseLocations(), courseDefUpdateDate);
      this.courseDefUpdatedNotifiers = new ArrayList<>();
   }

   /**
    * Prepares and starts the scheduler which then does automatically update the {@link CourseDef} in a well-defined
    * period
    * If there was never an update or the update is too old, then this method starts an update immediately!
    */
   public void startScheduler() {
      LocalDateTime nextCourseDefUpdate = this.courseDefUpdaterScheduler.startScheduler();
      statisticsHelper.setNextCourseDefUpdate(nextCourseDefUpdate);
      if (statisticsHelper.needsCourseDefUpdate()) {
         AquabasileaCourseBookerConfigImport configImport = YamlUtil.readYamlIgnoreMissingFile(configFile, AquabasileaCourseBookerConfigImport.class);
         this.updateCourseDefsAsRunnable(configImport.getDefaultCourseLocations());
      }
   }

   /**
    * Updates all {@link CourseDef} according to the aquabasliea-courses which are currently defined on their course-page
    *
    * @param courseLocations the locations to consider
    */
   public void updateAquabasileaCourses(List<CourseLocation> courseLocations) {
      if (isCourseDefUpdateRunning()) {
         LOG.warn("CourseDefs are already being updated, do nothing!");
         return;
      }
      this.executorService.submit(() -> updateCourseDefsAsRunnable(courseLocations));
   }

   CourseDefUpdaterScheduler getCourseDefUpdaterScheduler() {
      return courseDefUpdaterScheduler;
   }

   private void updateCourseDefsAsRunnable(List<CourseLocation> courseLocations) {
      try {
         isCourseDefUpdateRunning = true;
         updateAquabasileaCourseBookerConfig(courseLocations, configFile);
         LOG.info("Updating course-defs..");
         LocalDateTime start = LocalDateTime.now();
         updateAquabasileaCoursesInternal(courseLocations);
         Duration duration = Duration.ofMillis(start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
         LOG.info("Updating course-defs done, duration: {}", duration);
         updateStatistics(start);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         this.isCourseDefUpdateRunning = false;
      }
   }

   private void updateStatistics(LocalDateTime dateWhenUpdateStarted) {
      Duration durationUntilNextUpdate = courseDefUpdaterScheduler.calcDelayUntilNextUpdate();
      statisticsHelper.setLastCourseDefUpdate(dateWhenUpdateStarted);
      statisticsHelper.setNextCourseDefUpdate(dateWhenUpdateStarted.plusNanos(durationUntilNextUpdate.toNanos()));
   }

   private void updateAquabasileaCoursesInternal(List<CourseLocation> courseLocations) {
      List<com.aquabasilea.web.model.CourseLocation> webCourseLocations = map2WebCourseLocation(courseLocations);
      ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaCourseExtractorSupplier.get().extractAquabasileaCourses(webCourseLocations);
      courseDefRepository.deleteAll();
      List<CourseDef> courseDefs = mapAquabasileaCourse2CourseDefs(extractedAquabasileaCourses);
      courseDefRepository.saveAll(courseDefs);
      courseDefUpdatedNotifiers.forEach(courseDefUpdatedNotifier -> courseDefUpdatedNotifier.courseDefsUpdated(courseDefs));
   }

   public synchronized boolean isCourseDefUpdateRunning() {
      return isCourseDefUpdateRunning;
   }

   public void addCourseDefUpdatedNotifier(CourseDefUpdatedNotifier courseDefUpdatedNotifier) {
      courseDefUpdatedNotifiers.add(Objects.requireNonNull(courseDefUpdatedNotifier));
   }

   private List<CourseDef> mapAquabasileaCourse2CourseDefs(ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      return coursesDefEntityMapper.mapAquabasileaCourse2CourseDefs(extractedAquabasileaCourses.getAquabasileaCourses())
              .stream()
              .distinct()
              .toList();
   }

   @NotNull
   private static List<com.aquabasilea.web.model.CourseLocation> map2WebCourseLocation(List<CourseLocation> courseLocations) {
      return courseLocations.stream()
              .map(CourseLocation::getWebCourseLocation)
              .collect(Collectors.toList());
   }

   private static void updateAquabasileaCourseBookerConfig(List<CourseLocation> courseLocations, String configFile) {
      LOG.info("Updating configuration..");
      AquabasileaCourseBookerConfigImport configImport = AquabasileaCourseBookerConfigImport.readFromFile(configFile);
      configImport.setDefaultCourses(courseLocations.stream()
              .map(CourseLocation::getCourseLocationName)
              .toList());
      configImport.save2File();
      LOG.info("Configuration updated!");
   }
}
