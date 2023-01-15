package com.aquabasilea.persistence.config;

import com.aquabasilea.app.config.AquabasileaCourseBookerAppConfig;
import com.aquabasilea.app.config.AquabasileaCourseBookerPersistenceConfig;
import com.brugalibre.common.domain.app.config.CommonAppPersistenceConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@EnableAutoConfiguration
@TestPropertySource(locations="classpath:application-test.yml")
@ActiveProfiles("test")
@Configuration
@Import({AquabasileaCourseBookerPersistenceConfig.class, AquabasileaCourseBookerAppConfig.class, CommonAppPersistenceConfig.class})
public class TestAquabasileaCourseBookerPersistenceConfig {
}
