package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros;

import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseLocationExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping.MigrosCourseMapper;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.migrosapi.api.v1.model.getcenters.request.MigrosApiGetCentersRequest;
import com.aquabasilea.migrosapi.api.v1.model.getcenters.response.MigrosApiGetCentersResponse;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiCourseLocationsExtractor} hides the migros-api specific implementation of a {@link CourseDefExtractorFacade}
 */
public class MigrosApiCourseLocationsExtractor implements CourseLocationExtractorFacade {
   private final MigrosCourseMapper migrosCourseMapper;
   private final MigrosApi migrosApi;

   public MigrosApiCourseLocationsExtractor(MigrosApi migrosApi, MigrosCourseMapper migrosCourseMapper) {
      this.migrosCourseMapper = migrosCourseMapper;
      this.migrosApi = migrosApi;
   }

   public List<CourseLocation> getCourseLocations() {
      MigrosApiGetCentersRequest migrosApiGetCentersRequest = getMigrosApiGetCentersRequest();
      MigrosApiGetCentersResponse migrosApiGetCentersResponse = migrosApi.getCenters(migrosApiGetCentersRequest);
      return migrosCourseMapper.mapMigrosCenters2CourseLocations(migrosApiGetCentersResponse.centers());
   }

   private static MigrosApiGetCentersRequest getMigrosApiGetCentersRequest() {
      // so far nothing configurable
      return new MigrosApiGetCentersRequest();
   }
}
