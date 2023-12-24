package com.aquabasilea.migrosapi.api.v1.model.getcenters.response;

import com.aquabasilea.migrosapi.api.v1.model.getcenters.request.MigrosApiGetCentersRequest;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiGetCentersResponse} defines the response from the {@link MigrosApi} for a
 * {@link MigrosApi#getCenters(MigrosApiGetCentersRequest)}
 *
 * @param centers the found {@link MigrosCenter}s
 */
public record MigrosApiGetCentersResponse(List<MigrosCenter> centers) {
}
