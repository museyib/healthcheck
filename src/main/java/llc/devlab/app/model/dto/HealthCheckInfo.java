package llc.devlab.app.model.dto;

import llc.devlab.app.enums.HealthStatus;

public record HealthCheckInfo(HealthStatus status, Object components) {
}
