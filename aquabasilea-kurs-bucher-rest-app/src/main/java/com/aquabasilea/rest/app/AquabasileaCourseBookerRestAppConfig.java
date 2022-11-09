package com.aquabasilea.rest.app;

import com.aquabasilea.app.config.AquabasileaCourseBookerAppConfig;
import com.brugalibre.common.domain.app.config.CommonAppPersistenceConfig;
import com.brugalibre.common.security.app.config.CommonAppSecurityConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.aquabasilea.rest", "com.aquabasilea.search"})
@EntityScan(basePackages = {"com.brugalibre.persistence"})
@Import({AquabasileaCourseBookerAppConfig.class, CommonAppSecurityConfig.class, CommonAppPersistenceConfig.class})
public class AquabasileaCourseBookerRestAppConfig {
   // no-op
}

