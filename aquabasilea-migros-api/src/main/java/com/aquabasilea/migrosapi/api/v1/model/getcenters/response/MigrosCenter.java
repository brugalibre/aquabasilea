package com.aquabasilea.migrosapi.api.v1.model.getcenters.response;

/**
 * A {@link MigrosCenter} defines a guided course which can be booked at a certain day and a certain time
 *
 * @param centerId    the internal, unique id of the center
 * @param centerName  the name of the center
 * @param centerGroup the name of the group, to which this center belongs (e.g. Activ Fitness, Fitnesscenter, Fitnesspark)
 */
public record MigrosCenter(String centerId, String centerName, String centerGroup) {
   // no-op
}

