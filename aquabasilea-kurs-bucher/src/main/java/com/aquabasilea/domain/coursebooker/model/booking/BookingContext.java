package com.aquabasilea.domain.coursebooker.model.booking;

/**
 * Defines the context in which a booking is executed
 *
 * @param dryRun <code>true</code> if the current run is only for testing purpose without actual booking any courses or <code>false</code> if not
 */
public record BookingContext(boolean dryRun) {
   // no-op
}
