package com.aquabasilea.course.repository.yaml.impl;

import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.util.YamlUtil;

import static java.util.Objects.requireNonNull;

/**
 * A yaml-based repository for a {@link WeeklyCourses}
 */
public class YamlWeeklyCoursesRepositoryImpl implements WeeklyCoursesRepository {
   private final String ymlFile;

   public YamlWeeklyCoursesRepositoryImpl(String ymlFile) {
      this.ymlFile = requireNonNull(ymlFile);
   }

   @Override
   public WeeklyCourses findFirstWeeklyCourses() {
      return YamlUtil.readYaml(ymlFile, WeeklyCourses.class);
   }

   @Override
   public WeeklyCourses save(WeeklyCourses weeklyCourses) {
      YamlUtil.save2File(weeklyCourses, ymlFile);
      return findFirstWeeklyCourses();
   }
}
