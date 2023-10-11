package com.aquabasilea.rest.config;

import com.aquabasilea.rest.app.config.AquabasileaCourseBookerRestAppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AquabasileaCourseBookerRestAppConfig.class})
public class TestAquabasileaCourseBookerRestAppConfig {
   // no-op
}
