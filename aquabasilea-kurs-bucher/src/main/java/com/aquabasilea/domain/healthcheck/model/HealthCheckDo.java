package com.aquabasilea.domain.healthcheck.model;

import com.aquabasilea.util.DateUtil;
import com.brugalibre.common.domain.model.DomainModel;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * The HealthCheckResultDo contains very simplified information weather when the last health-check
 * was success or failed.
 * If the {@link HealthCheckDo#lastFailed} is <code>null</code> and only the
 * {@link HealthCheckDo#lastSuccessful} is filled, then the health check is considered healthy.
 * <p>
 * If the {@link HealthCheckDo#lastFailed} is not <code>null</code> then the last health check was failed. In that
 * case the field * {@link HealthCheckDo#lastSuccessful} provides information about the last time the health-check succeeded
 */
public class HealthCheckDo implements DomainModel {
    public static final String HEALTH_CHECK_FAILED_MSG = "Health-check failed, last time at %s";
    private LocalDateTime lastFailed;
    private LocalDateTime lastSuccessful;

    private String id;

    public HealthCheckDo() {
        super();
    }

    public LocalDateTime getLastFailed() {
        return lastFailed;
    }

    public void setLastFailed(LocalDateTime lastFailed) {
        this.lastFailed = lastFailed;
    }

    public LocalDateTime getLastSuccessful() {
        return lastSuccessful;
    }

    public void setLastSuccessful(LocalDateTime lastSuccessful) {
        this.lastSuccessful = lastSuccessful;
    }

    /**
     * <b>Note</b> This method returns {@link HealthCheckResult#successful()} even if there was never a health check..
     *
     * @return a {@link HealthCheckResult}
     */
    public HealthCheckResult getHealthCheckResult() {
        if (lastFailed != null) {
            return HealthCheckResult.failed(HEALTH_CHECK_FAILED_MSG.formatted(DateUtil.toString(lastFailed, Locale.getDefault())));
        }
        return HealthCheckResult.successful();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HealthCheckDo{" +
                "lastFailed=" + lastFailed +
                ", lastSuccessful=" + lastSuccessful +
                ", id='" + id + '\'' +
                '}';
    }
}
