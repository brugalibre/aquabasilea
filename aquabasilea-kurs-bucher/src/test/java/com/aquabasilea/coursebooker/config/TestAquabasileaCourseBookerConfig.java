package com.aquabasilea.coursebooker.config;

import java.time.Duration;

public class TestAquabasileaCourseBookerConfig extends AquabasileaCourseBookerConfig {
   public TestAquabasileaCourseBookerConfig(Duration duration2StartDryRunEarlier, Duration duration2StartBookerEarlier) {
      setDurationToStartDryRunEarlier(duration2StartDryRunEarlier);
      setDurationToStartBookerEarlier(duration2StartBookerEarlier);
   }
}
