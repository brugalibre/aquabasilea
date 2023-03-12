package com.aquabasilea.migrosapi.model.getcourse.response.api;

import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;
import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;

import java.util.List;

/**
 * The {@link MigrosApiGetCoursesResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#bookCourse(AuthenticationContainer, MigrosApiBookCourseRequest)} call
 *
 * @param courses the found {@link MigrosCourse}
 */
public record MigrosApiGetCoursesResponse(List<MigrosCourse> courses) {
}
