package com.aquabasilea.migrosapi.api.v1.model.getcourse.response;

import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiGetBookedCoursesResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#getBookedCourses(AuthenticationContainer)}
 *
 * @param courses the found {@link MigrosCourse}
 */
public record MigrosApiGetBookedCoursesResponse(List<MigrosCourse> courses) {
}
