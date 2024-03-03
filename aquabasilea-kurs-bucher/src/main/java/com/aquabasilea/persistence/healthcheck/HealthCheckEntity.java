package com.aquabasilea.persistence.healthcheck;


import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "healthcheck")
public class HealthCheckEntity extends DomainEntity {

    @Nullable
    private LocalDateTime lastFailed;

    @Nullable
    private LocalDateTime lastSuccessful;

    public HealthCheckEntity(String id) {
        super(id);
    }

    public HealthCheckEntity() {
        super(null);
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

    @Override
    public String toString() {
        return "HealthCheckEntity{" +
                "lastFailed=" + lastFailed +
                ", lastSuccessful=" + lastSuccessful +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HealthCheckEntity that = (HealthCheckEntity) o;
        return Objects.equals(lastFailed, that.lastFailed) && Objects.equals(lastSuccessful, that.lastSuccessful);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lastFailed, lastSuccessful);
    }
}
