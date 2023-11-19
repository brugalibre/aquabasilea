package com.aquabasilea.domain.coursedef.update.facade;

import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;

import java.util.function.Supplier;

/**
 * The {@link CourseExtractorFacadeFactory} creates new {@link CourseExtractorFacade}
 */
public class CourseExtractorFacadeFactory {

   /**
    * Creates a new {@link CourseExtractorFacade} for the given {@link AquabasileaCourseBookerConfig}. This also creates a new
    * {@link MigrosApi}. The recommendation is that there should only be one {@link CourseExtractorFacade} per application
    *
    * @param aquabasileaCourseBookerConfig the configuration which is applied to the underlying {@link CourseExtractorFacade}
    * @return a new created {@link CourseExtractorFacade}
    */
   public static CourseExtractorFacade getCourseExtractorFacade(AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      MigrosApi migrosApi = new MigrosApiImpl(getReadOnlyBearerTokenProvider());
      Supplier<AquabasileaCourseExtractor> courseExtractorSupplier = () -> AquabasileaCourseExtractorImpl
              .createAndInitAquabasileaWebNavigator(aquabasileaCourseBookerConfig.getCourseConfigFile());
      return new CourseExtractorFacade(courseExtractorSupplier, () -> migrosApi, aquabasileaCourseBookerConfig);
   }

   private static BearerTokenProvider getReadOnlyBearerTokenProvider() {
      return (username, userPwd) -> {
         throw new IllegalStateException("Read only MigrosApi!");
      };
   }
}
