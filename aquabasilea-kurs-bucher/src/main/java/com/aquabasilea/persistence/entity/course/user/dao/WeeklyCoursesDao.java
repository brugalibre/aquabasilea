package com.aquabasilea.persistence.entity.course.user.dao;

import com.aquabasilea.persistence.entity.course.user.WeeklyCoursesEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WeeklyCoursesDao extends CrudRepository<WeeklyCoursesEntity, UUID> {
}
