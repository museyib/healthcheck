package llc.devlab.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private boolean enabled;

    @ManyToMany
    @JoinTable(name = "service_dependency",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id"))
    private Set<MonitoredService> dependencies = new LinkedHashSet<>();
}
