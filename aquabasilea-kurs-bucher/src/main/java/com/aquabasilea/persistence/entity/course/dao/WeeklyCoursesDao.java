package com.aquabasilea.persistence.entity.course.dao;

import com.aquabasilea.persistence.entity.course.WeeklyCoursesEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WeeklyCoursesDao extends CrudRepository<WeeklyCoursesEntity, UUID> {
}
