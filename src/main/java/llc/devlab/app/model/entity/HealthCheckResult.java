package llc.devlab.app.model.entity;

import jakarta.persistence.*;
import llc.devlab.app.enums.HealthStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "health_check_result")
public class HealthCheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private MonitoredService service;
    @Enumerated(EnumType.STRING)
    private HealthStatus status;
    private Integer statusCode;
    private Instant checkedAt = Instant.now();
    private int responseTimeMs;
}