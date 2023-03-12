package com.aquabasilea.coursebooker.service.booking.facade;

import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.service.booking.apimigros.MigrosApiCourseBookerFactory;
import com.aquabasilea.coursebooker.service.booking.apimigros.MigrosApiProvider;
import com.aquabasilea.coursebooker.service.booking.webmigros.MigrosWebCourseBookerFacadeImpl;
import com.aquabasilea.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class AquabasileaCourseBookerFacadeFactory {

   private final MigrosApiCourseBookerFactory migrosApiCourseBookerFactory;
   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;

   @Autowired
   public AquabasileaCourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider) {
      this(migrosApiProvider, new AquabasileaCourseBookerConfig());
   }

   public AquabasileaCourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider, AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      this.migrosApiCourseBookerFactory = new MigrosApiCourseBookerFactory(migrosApiProvider);
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig;
   }

   public AquabasileaCourseBookerFacade createNewAquabasileaCourseBookerFacade(String username, Supplier<char[]> userPassword,
                                                                               Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      aquabasileaCourseBookerConfig.refresh();
      if (aquabasileaCourseBookerConfig.getAquabasileaCourseBookerType() == AquabasileaCourseBookerType.AQUABASILEA_WEB) {
         return getAquabasileaWebNavigator(username, userPassword, duration2WaitUntilCourseBecomesBookable);
      }
      return migrosApiCourseBookerFactory.createMigrosApiCourseBookerImpl(username, userPassword, duration2WaitUntilCourseBecomesBookable);
   }

   private static AquabasileaCourseBookerFacade getAquabasileaWebNavigator(String username, Supplier<char[]> userPassword,
                                                                           Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return new MigrosWebCourseBookerFacadeImpl(username, userPassword, duration2WaitUntilCourseBecomesBookable);
   }
}
