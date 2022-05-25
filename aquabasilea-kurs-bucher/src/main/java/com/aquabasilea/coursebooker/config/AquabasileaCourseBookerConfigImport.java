package com.aquabasilea.coursebooker.config;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.aquabasilea.course.CourseLocation.FITNESSPARK_HEUWAAGE;
import static com.aquabasilea.course.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static java.util.Objects.nonNull;

public class AquabasileaCourseBookerConfigImport {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerConfigImport.class);
   private Integer minutesToStartDryRunEarlier;
   private List<String> defaultCourses;
   private String fileLocation;

   public AquabasileaCourseBookerConfigImport() {
      this.defaultCourses = List.of(MIGROS_FITNESSCENTER_AQUABASILEA.getCourseLocationName(), FITNESSPARK_HEUWAAGE.getCourseLocationName());
   }

   /**
    * Loads a {@link AquabasileaCourseBookerConfigImport} from the given input file
    *
    * @param configFile the config-file location
    * @return a {@link AquabasileaCourseBookerConfigImport} from the given input file
    */
   public static AquabasileaCourseBookerConfigImport readFromFile(String configFile) {
      AquabasileaCourseBookerConfigImport aquabasileaCourseBookerConfigImport = YamlUtil.readYamlIgnoreMissingFile(configFile, AquabasileaCourseBookerConfigImport.class);
      aquabasileaCourseBookerConfigImport.fileLocation = configFile;
      return aquabasileaCourseBookerConfigImport;
   }

   public Integer getMinutesToStartDryRunEarlier() {
      return minutesToStartDryRunEarlier;
   }

   public void setMinutesToStartDryRunEarlier(Integer minutesToStartDryRunEarlier) {
      this.minutesToStartDryRunEarlier = minutesToStartDryRunEarlier;
   }

   public List<CourseLocation> getDefaultCourseLocations() {
      return defaultCourses.stream()
              .map(CourseLocation::of)
              .toList();
   }

   public void setDefaultCourses(List<String> defaultCourses) {
      if (nonNull(defaultCourses)) {
         this.defaultCourses = defaultCourses;
      }
   }

   /**
    * Saves this {@link AquabasileaCourseBookerConfigImport} to the given destination
    */
   public void save2File() {
//      YamlUtil.save2File(this, fileLocation, (e, obj) -> LOG.error("Unable to store object '" + obj + "' to location '" + fileLocation + "'!"));
   }
}
