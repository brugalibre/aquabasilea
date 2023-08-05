package com.aquabasilea.domain.coursebooker.config;

import java.time.Duration;

public class TestAquabasileaCourseBookerConfig extends AquabasileaCourseBookerConfig {
   public TestAquabasileaCourseBookerConfig() {
      super(null);
   }


   @Override
   public AquabasileaCourseBookerConfig refresh() {
      // no refresh otherwise we'll loos the values we set earlier in the constructor!
      return this;
   }

   public TestAquabasileaCourseBookerConfig(String weeklyCoursesYml, Duration duration2StartDryRunEarlier,
                                            Duration duration2StartBookerEarlier) {
      super(weeklyCoursesYml);
      setDurationToStartDryRunEarlier(duration2StartDryRunEarlier);
      setDurationToStartBookerEarlier(duration2StartBookerEarlier);
      setDaysToBookCourseEarlier(0);
   }
}
