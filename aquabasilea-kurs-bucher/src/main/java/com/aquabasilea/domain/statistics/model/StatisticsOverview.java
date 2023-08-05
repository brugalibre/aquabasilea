package com.aquabasilea.domain.statistics.model;

/**
 * THe StatisticsOverview is actualy a {@link Statistics} object with the additional
 * information about the success rate, which is not persistent
 *
 * @param statistics          the {@link Statistics} entity
 * @param totalBookingCounter the total amount of bookings
 * @param bookingSuccessRate  the overall success rate
 */
public record StatisticsOverview(Statistics statistics, int totalBookingCounter, double bookingSuccessRate) {
}
