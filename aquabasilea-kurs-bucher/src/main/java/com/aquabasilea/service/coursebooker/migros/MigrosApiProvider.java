package com.aquabasilea.service.coursebooker.migros;

import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseLocationExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.AuthenticationContainerRegistry;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.MigrosApiCourseDefExtractor;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.MigrosApiCourseLocationsExtractor;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping.MigrosCourseMapper;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.security.AquabasileaWebBearerTokenProviderImpl;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.security.MultiUserBearerTokenProvider;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.migrosapi.api.service.MigrosServiceFactory;
import com.aquabasilea.migrosapi.api.service.configuration.ServiceConfiguration;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import com.aquabasilea.migrosapi.api.v1.service.security.bearertoken.BearerTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

/**
 * The {@link MigrosApiProvider} is a bean providing a collection of essential migros-services
 */
@Service
public class MigrosApiProvider {
   private final MigrosApi migrosApi;
   private final MigrosApiCourseDefExtractor migrosApiCourseDefExtractor;
   private final AuthenticationContainerRegistry authenticationContainerRegistry;
   private final CourseLocationExtractorFacade migrosApiCourseLocationExtractorFacade;
   private final MigrosCourseMapper migrosCourseMapper;

   @Autowired
   public MigrosApiProvider(@Value("${application.security.bearerTokenTtl:0}") int ttl,
                            @Value("${application.configuration.course-booker-config}") String propertiesFile,
                            AuthenticationContainerRegistry authenticationContainerRegistry,
                            CourseLocationRepository courseLocationRepository,
                            ServiceConfiguration configuration) {
      BearerTokenProvider bearerTokenProvider = getBearerTokenProvider(ttl, propertiesFile, configuration);
      this.authenticationContainerRegistry = authenticationContainerRegistry;
      this.migrosApi = MigrosServiceFactory.INSTANCE.getMigrosApiV1(bearerTokenProvider, configuration);
      this.migrosCourseMapper = MigrosCourseMapper.of(courseLocationRepository);
      this.migrosApiCourseDefExtractor = new MigrosApiCourseDefExtractor(migrosApi, authenticationContainerRegistry, migrosCourseMapper);
      this.migrosApiCourseLocationExtractorFacade = new MigrosApiCourseLocationsExtractor(migrosApi, migrosCourseMapper);
   }

   public MigrosApiProvider(MigrosApi migrosApi, MigrosApiCourseDefExtractor migrosApiCourseDefExtractor,
                            AuthenticationContainerRegistry authenticationContainerRegistry,
                            CourseLocationRepository courseLocationRepository,
                            CourseLocationExtractorFacade migrosApiCourseLocationExtractorFacade) {
      this.migrosApi = requireNonNull(migrosApi);
      this.migrosApiCourseDefExtractor = migrosApiCourseDefExtractor;
      this.authenticationContainerRegistry = authenticationContainerRegistry;
      this.migrosCourseMapper = MigrosCourseMapper.of(courseLocationRepository);
      this.migrosApiCourseLocationExtractorFacade = migrosApiCourseLocationExtractorFacade;
   }

   public MigrosApi getMigrosApi() {
      return migrosApi;
   }

   public MigrosApiCourseDefExtractor getMigrosApiCourseDefExtractorFacade() {
      return migrosApiCourseDefExtractor;
   }

   public CourseLocationExtractorFacade getMigrosApiCourseLocationExtractorFacade() {
      return migrosApiCourseLocationExtractorFacade;
   }

   public AuthenticationContainer getAuthenticationContainerForUserId(String userId) {
      return authenticationContainerRegistry.getAuthenticationContainerForUserId(userId);
   }

   public MigrosCourseMapper getMigrosCourseMapper() {
      return migrosCourseMapper;
   }

   private static BearerTokenProvider getBearerTokenProvider(int ttl, String propertiesFile, ServiceConfiguration configuration) {
      MultiUserBearerTokenProvider bearerTokenProvider = new MultiUserBearerTokenProvider(() -> new AquabasileaWebBearerTokenProviderImpl(propertiesFile));
      return MigrosServiceFactory.INSTANCE.createAutoRenewBearerTokenProviderV1(bearerTokenProvider, configuration, ttl);
   }
}
