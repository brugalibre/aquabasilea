package com.aquabasilea.coursedef.model.repository.mapping;

import com.aquabasilea.coursebooker.model.course.CourseLocation;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.coursedef.persistence.CourseDefEntity;
import com.aquabasilea.migrosapi.model.response.api.MigrosCourse;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import com.brugalibre.domain.user.mapper.UserEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = UserEntityMapper.class)
public interface CoursesDefEntityMapper extends CommonDomainModelMapper<CourseDef, CourseDefEntity> {

   List<CourseDef> mapAquabasileaCourses2CourseDefs(List<AquabasileaCourse> aquabasileaCourses);

   @Mapping(source = "courseLocation", target = "courseLocation", qualifiedByName = "mapCourseLocationName2CourseLocation")
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "userId", ignore = true)
   CourseDef mapAquabasileaCourse2CourseDef(AquabasileaCourse aquabasileaCourse);

   @Named("mapCourseLocationName2CourseLocation")
   default CourseLocation mapCourseLocationName2CourseLocation(String centerId) {
      return CourseLocation.fromDisplayName(centerId);
   }

   List<CourseDef> mapMigrosCourses2CourseDefs(List<MigrosCourse> migrosCourses);

   @Mapping(source = "centerId", target = "courseLocation", qualifiedByName = "mapCenterId2CourseLocation")
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "userId", ignore = true)
   CourseDef mapMigrosCourse2CourseDef(MigrosCourse migrosCourse);

   @Named("mapCenterId2CourseLocation")
   default CourseLocation mapCenterId2CourseLocation(String centerId) {
      return CourseLocation.fromId(centerId);
   }

}