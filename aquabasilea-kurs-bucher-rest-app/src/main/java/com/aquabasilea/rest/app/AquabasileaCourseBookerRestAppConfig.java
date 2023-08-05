package com.aquabasilea.rest.app;

import com.aquabasilea.application.config.AquabasileaCourseBookerAppConfig;
import com.brugalibre.common.domain.app.config.CommonAppPersistenceConfig;
import com.brugalibre.common.security.app.config.CommonAppSecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.aquabasilea.rest", "com.aquabasilea.search"})
@Import({AquabasileaCourseBookerAppConfig.class, CommonAppSecurityConfig.class, CommonAppPersistenceConfig.class})
public class AquabasileaCourseBookerRestAppConfig {
   // no-op
}

