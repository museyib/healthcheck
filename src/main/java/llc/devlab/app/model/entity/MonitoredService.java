package llc.devlab.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "monitored_service")
public class MonitoredService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String url;
    private int checkInterval;
    private boolean critical;
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
    private Instant lastCheckedAt;
}