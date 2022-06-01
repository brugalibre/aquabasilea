package com.aquabasilea.model.course.coursedef.repository.mapping;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CoursesDefEntityMapper {

   @Mapping(target = "id", ignore = true)
   CourseDefEntity map2CourseDefEntity(CourseDef courseDef);

   List<CourseDefEntity> map2CourseDefEntities(List<CourseDef> courseDefs);

   CourseDef map2CourseDef(CourseDefEntity courseDefEntity);

   List<CourseDef> mapAquabasileaCourse2CourseDefs(List<AquabasileaCourse> aquabasileaCourses);

   List<CourseDef> map2CourseDefs(List<CourseDefEntity> courseDefEntities);
}