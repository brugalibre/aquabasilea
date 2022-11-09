package com.aquabasilea.model.course.coursedef.repository.mapping;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import com.brugalibre.domain.user.mapper.UserEntityMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = UserEntityMapper.class)
public interface CoursesDefEntityMapper extends CommonDomainModelMapper<CourseDef, CourseDefEntity> {

   List<CourseDef> mapAquabasileaCourses2CourseDefs(List<AquabasileaCourse> aquabasileaCourses);
}