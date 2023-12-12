package com.aquabasilea.domain.coursebooker.booking.facade;

import com.aquabasilea.domain.coursebooker.booking.apimigros.MigrosApiCourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.booking.webmigros.MigrosWebCourseBookerFacadeImpl;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;

import java.time.Duration;
import java.util.function.Supplier;

public class AquabasileaCourseBookerFacadeFactory {

   private final MigrosApiCourseBookerFacadeFactory migrosApiCourseBookerFacadeFactory;
   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;

   public AquabasileaCourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider, String propertiesFile) {
      this(migrosApiProvider, new AquabasileaCourseBookerConfig(propertiesFile));
   }

   public AquabasileaCourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider, AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      this.migrosApiCourseBookerFacadeFactory = new MigrosApiCourseBookerFacadeFactory(migrosApiProvider);
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig;
   }

   public AquabasileaCourseBookerFacade createNewAquabasileaCourseBookerFacade(String username, Supplier<char[]> userPassword,
                                                                               Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      aquabasileaCourseBookerConfig.refresh();
      if (aquabasileaCourseBookerConfig.getAquabasileaCourseBookerType() == AquabasileaCourseBookerType.AQUABASILEA_WEB) {
         return getAquabasileaWebNavigator(username, userPassword, duration2WaitUntilCourseBecomesBookable);
      }
      return migrosApiCourseBookerFacadeFactory.createMigrosApiCourseBookerImpl(username, userPassword, duration2WaitUntilCourseBecomesBookable);
   }

   private AquabasileaCourseBookerFacade getAquabasileaWebNavigator(String username, Supplier<char[]> userPassword,
                                                                           Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return new MigrosWebCourseBookerFacadeImpl(username, userPassword, duration2WaitUntilCourseBecomesBookable,
              aquabasileaCourseBookerConfig.getCourseConfigFile());
   }
}
