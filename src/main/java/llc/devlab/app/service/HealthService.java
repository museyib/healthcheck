package llc.devlab.app.service;

import llc.devlab.app.enums.HealthStatus;
import llc.devlab.app.model.entity.HealthCheckResult;
import llc.devlab.app.model.entity.MonitoredService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {

    private final RestClient restClient;
    private final ServiceMonitoringService serviceMonitoringService;

    public void checkStatus(List<MonitoredService> services) {
        for (MonitoredService service : services) {
            checkService(service);
        }
    }

    private void checkService(MonitoredService service) {
        HealthCheckResult result = executeHealthCheck(service);

        serviceMonitoringService.saveHealthCheckResult(result, service);
    }

    private HealthCheckResult executeHealthCheck(MonitoredService service) {

        long startTime = System.nanoTime();

        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(service.getUrl())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (_, _) -> {})
                    .toEntity(String.class);

            HealthStatus status = response.getStatusCode().is2xxSuccessful()
                    ? HealthStatus.UP
                    : HealthStatus.DOWN;

            return buildResult(
                    service,
                    status,
                    response.getStatusCode().value(),
                    elapsedMs(startTime)
            );

        } catch (ResourceAccessException ex) {
            log.warn("Could not reach monitored service '{}': {}", service.getName(), ex.getMessage());

            return buildResult(
                    service,
                    HealthStatus.DOWN,
                    null,
                    elapsedMs(startTime)
            );

        } catch (RestClientException ex) {
            log.warn("HTTP check failed for '{}': {}", service.getName(), ex.getMessage());

            return buildResult(
                    service,
                    HealthStatus.DOWN,
                    null,
                    elapsedMs(startTime)
            );
        }
    }

    private HealthCheckResult buildResult(
            MonitoredService service,
            HealthStatus status,
            Integer statusCode,
            int responseTimeMs) {

        HealthCheckResult result = new HealthCheckResult();
        result.setService(service);
        result.setStatus(status);
        result.setStatusCode(statusCode);
        result.setResponseTimeMs(responseTimeMs);
        return result;
    }

    private int elapsedMs(long startTime) {
        return (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
