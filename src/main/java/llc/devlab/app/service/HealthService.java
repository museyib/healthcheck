package llc.devlab.app.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import llc.devlab.app.enums.HealthStatus;
import llc.devlab.app.model.entity.HealthCheckResult;
import llc.devlab.app.model.entity.MonitoredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HealthService {

    private static final int CRITICAL_SERVICE_DOWN_STATUS_ALERT_THRESHOLD = 10;

    private final Counter healthCheckCounter;
    private final Counter healthCheckCounterUp;
    private final Counter healthCheckCounterDown;
    private final RestClient restClient;
    private final ServiceMonitoringService serviceMonitoringService;
    private final Map<Long, Integer> statusMap;

    public HealthService(RestClient restClient,
                         ServiceMonitoringService serviceMonitoringService,
                         MeterRegistry meterRegistry) {
        this.restClient = restClient;
        this.serviceMonitoringService = serviceMonitoringService;
        this.healthCheckCounter = meterRegistry.counter("health_check_counter");
        this.healthCheckCounterUp = meterRegistry.counter("health_check_counter_up");
        this.healthCheckCounterDown = meterRegistry.counter("health_check_counter_down");
        this.statusMap = new HashMap<>();
    }

    public void checkStatus(List<MonitoredService> services) {
        for (MonitoredService service : services) {
            checkService(service);
        }
    }

    private void checkService(MonitoredService service) {
        healthCheckCounter.increment();
        HealthCheckResult result = executeHealthCheck(service);

        serviceMonitoringService.saveHealthCheckResult(result, service);

        if (HealthStatus.DOWN.equals(result.getStatus())) {
            healthCheckCounterDown.increment();

            if (service.isCritical()) {
                statusMap.put(service.getId(), statusMap.getOrDefault(service.getId(), 0) + 1);
                long downCount = statusMap.get(service.getId());
                if (downCount == CRITICAL_SERVICE_DOWN_STATUS_ALERT_THRESHOLD) {
                    log.error("Critical number of service down: {}", service.getId());
                }
            }
        } else {
            healthCheckCounterUp.increment();
            statusMap.remove(service.getId());
        }
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
