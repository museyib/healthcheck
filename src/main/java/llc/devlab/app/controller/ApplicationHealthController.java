package llc.devlab.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class ApplicationHealthController {
    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @Cacheable("healthCache")
    public HealthDescriptor getHealth() {
        return healthEndpoint.health();
    }

    @GetMapping("/deep")
    @CacheEvict(value = "healthCache", allEntries = true)
    public HealthDescriptor getHealthDeep() {
        return healthEndpoint.health();
    }
}
