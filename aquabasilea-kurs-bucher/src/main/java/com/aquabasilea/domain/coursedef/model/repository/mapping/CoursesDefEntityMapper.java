package com.aquabasilea.domain.coursedef.model.repository.mapping;

import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.courselocation.mapper.CoursesLocationEntityMapper;
import com.aquabasilea.persistence.coursedef.CourseDefEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import com.brugalibre.domain.user.mapper.UserEntityMapper;
import org.mapstruct.Mapper;

@Mapper(uses = {UserEntityMapper.class, CoursesLocationEntityMapper.class})
public interface CoursesDefEntityMapper extends CommonDomainModelMapper<CourseDef, CourseDefEntity> {
   // no-op
}