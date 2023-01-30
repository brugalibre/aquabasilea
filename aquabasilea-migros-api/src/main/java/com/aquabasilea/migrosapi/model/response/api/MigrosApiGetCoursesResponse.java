package com.aquabasilea.migrosapi.model.response.api;

import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.migrosapi.model.request.api.MigrosApiBookCourseRequest;

import java.util.List;

/**
 * The {@link MigrosApiGetCoursesResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#bookCourse(MigrosApiBookCourseRequest)} call
 *
 * @param courses the found {@link MigrosCourse}
 */
public record MigrosApiGetCoursesResponse(List<MigrosCourse> courses) {
}
