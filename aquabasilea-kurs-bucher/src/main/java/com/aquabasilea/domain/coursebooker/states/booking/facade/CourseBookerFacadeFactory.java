package com.aquabasilea.domain.coursebooker.states.booking.facade;

import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.MigrosApiFacadeImpl;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;

import java.time.Duration;
import java.util.function.Supplier;

public class CourseBookerFacadeFactory {

   private final MigrosApiProvider migrosApiProvider;

   public CourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider) {
      this.migrosApiProvider = migrosApiProvider;
   }

   public CourseBookerFacade createCourseBookerFacade(String userId, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return createMigrosApiFacade(userId, duration2WaitUntilCourseBecomesBookable);
   }

   public CourseLocationExtractorFacade createCourseLocationExtractorFacade() {
      return createMigrosCourseLocationExtractorFacade();
   }

   private CourseLocationExtractorFacade createMigrosCourseLocationExtractorFacade() {
      return migrosApiProvider.getMigrosApiCourseLocationExtractorFacade();
   }

   private MigrosApiFacadeImpl createMigrosApiFacade(String userId, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return new MigrosApiFacadeImpl(migrosApiProvider.getMigrosApi(), migrosApiProvider.getMigrosApiCourseDefExtractorFacade(), migrosApiProvider.getMigrosCourseMapper(), () -> migrosApiProvider.getAuthenticationContainerForUserId(userId),
              duration2WaitUntilCourseBecomesBookable);
   }
}
