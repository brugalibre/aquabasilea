package com.aquabasilea.migrosapi.v1.model.getcourse.response;

import com.aquabasilea.migrosapi.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiGetCoursesResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#getCourses(MigrosApiGetCoursesRequest)}
 *
 * @param courses the found {@link MigrosCourse}
 */
public record MigrosApiGetCoursesResponse(List<MigrosCourse> courses) {
}
