package llc.devlab.app.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
            @Min(1) int connectTimeout,
            @Min(1) int readTimeout
    ) {}

    public record Dependency(
            @NotBlank String url
    ) {}
}
