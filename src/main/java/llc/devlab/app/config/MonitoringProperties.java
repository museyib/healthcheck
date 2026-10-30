package llc.devlab.app.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "monitoring")
public record MonitoringProperties(
        @Valid Scheduler scheduler,
        @Valid Http http,
        @Valid Dependency dependency
) {
    public record Scheduler(
            @Min(100) long pollInterval
    ) {}

    public record Http(
            @Min(1) Duration connectTimeout,
            @Min(1) Duration readTimeout
    ) {}

    public record Dependency(
            @NotBlank String url
    ) {}
}
