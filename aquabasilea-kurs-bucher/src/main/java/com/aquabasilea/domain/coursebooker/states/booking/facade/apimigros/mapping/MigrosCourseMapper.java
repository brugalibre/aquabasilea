package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.courselocation.mapper.CoursesLocationEntityMapper;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.migrosapi.api.v1.model.getcenters.response.MigrosCenter;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosCourse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = CoursesLocationEntityMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class MigrosCourseMapper {

   private CourseLocationRepository courseLocationRepository;

   public void setCourseLocationRepository(CourseLocationRepository courseLocationRepository) {
      this.courseLocationRepository = courseLocationRepository;
   }

   public abstract List<CourseLocation> mapMigrosCenters2CourseLocations(List<MigrosCenter> migrosCenters);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "name", source = "centerName")
   public abstract CourseLocation mapMigrosCenters2CourseLocation(MigrosCenter migrosCenter);

   public abstract List<CourseDef> mapMigrosCourses2CourseDefs(List<MigrosCourse> migrosCourses);

   @Mapping(source = "centerId", target = "courseLocation", qualifiedByName = "mapCenterId2CourseLocation")
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "userId", ignore = true)
   public abstract CourseDef mapMigrosCourse2CourseDef(MigrosCourse migrosCourse);

   @Named("mapCenterId2CourseLocation")
   protected CourseLocation mapCenterId2CourseLocation(String centerId) {
      return courseLocationRepository.findByCenterId(centerId);
   }

   public abstract List<Course> mapMigrosCourses2Courses(List<MigrosCourse> migrosCourses);

   @Mapping(target = "isPaused", ignore = true)
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   @Mapping(target = "hasCourseDef", ignore = true)
   @Mapping(source = "centerId", target = "courseLocation", qualifiedByName = "mapCenterId2CourseLocation")
   public abstract Course mapMigrosCourse2Course(MigrosCourse migrosCourse);

   /**
    * Creates a new {@link MigrosCourseMapper} with the given {@link CourseLocationRepository}
    *
    * @param courseLocationRepository the {@link CourseLocationRepository}
    * @return a  new {@link MigrosCourseMapper} with the given {@link CourseLocationRepository}
    */
   public static MigrosCourseMapper of(CourseLocationRepository courseLocationRepository) {
      MigrosCourseMapperImpl migrosCourseMapper = new MigrosCourseMapperImpl();
      migrosCourseMapper.setCourseLocationRepository(courseLocationRepository);
      return migrosCourseMapper;
   }
}