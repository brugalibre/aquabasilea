package com.aquabasilea.domain.healthcheck.model;

public record HealthCheckResult(boolean status, String message) {
    public static HealthCheckResult successful() {
        return new HealthCheckResult(true, null);
    }

    public static HealthCheckResult failed(String message) {
        return new HealthCheckResult(false, message);
    }

    public static HealthCheckResult failed() {
        return new HealthCheckResult(false, null);
    }
}
