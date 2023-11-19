package com.aquabasilea.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ConfigYamlFilePaths} contains paths of various configuration-yaml files which differs between develop-files
 * and production files. The actual path is read from the application.yaml file which used for starting the application
 * <p>
 */
public class ConfigYamlFilePaths {
   private static final Logger LOG = LoggerFactory.getLogger(ConfigYamlFilePaths.class);
   private final String courseBookerConfigFilePath;

   public ConfigYamlFilePaths(String courseBookerConfigFilePath) {
      this.courseBookerConfigFilePath = courseBookerConfigFilePath;
      LOG.info("Using value {} for 'courseBookerConfigFilePath'", courseBookerConfigFilePath);
   }

   public String getCourseBookerConfigFilePath() {
      return courseBookerConfigFilePath;
   }
}
