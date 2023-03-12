package com.aquabasilea.coursebooker.config;

import java.time.Duration;

public class TestAquabasileaCourseBookerConfig extends AquabasileaCourseBookerConfig {
   public TestAquabasileaCourseBookerConfig() {
      super(null);
   }

   public TestAquabasileaCourseBookerConfig(String weeklyCoursesYml, Duration duration2StartDryRunEarlier,
                                            Duration duration2StartBookerEarlier) {
      super(weeklyCoursesYml);
      setDurationToStartDryRunEarlier(duration2StartDryRunEarlier);
      setDurationToStartBookerEarlier(duration2StartBookerEarlier);
      setDaysToBookCourseEarlier(0);
   }
}
