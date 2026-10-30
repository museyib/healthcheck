package llc.devlab.app.health;

import llc.devlab.app.config.MonitoringProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component("externalHttp")
@RequiredArgsConstructor
public class ExternalHttpHealthIndicator implements HealthIndicator {

    private final RestClient restClient;
    private final MonitoringProperties properties;

    @Override
    public Health health() {
        try {
            HttpStatusCode status = restClient.get()
                    .uri(properties.dependency().url())
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

            if (status.is2xxSuccessful()) {
                return Health.up()
                        .withDetail("httpStatus", status.value())
                        .build();
            }

            return Health.down()
                    .withDetail("httpStatus", status.value())
                    .build();

        } catch (RestClientException ex) {
            return Health.down()
                    .withDetail("reason", "External dependency is unavailable")
                    .build();
        }
    }
}
