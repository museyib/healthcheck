package llc.devlab.app.service;

import llc.devlab.app.enums.HealthStatus;
import llc.devlab.app.model.dto.HealthResponse;
import llc.devlab.app.model.entity.HealthCheckResult;
import llc.devlab.app.model.entity.MonitoredService;
import llc.devlab.app.repository.HealthCheckResultRepository;
import llc.devlab.app.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceMonitoringService {
    private final HealthCheckResultRepository healthCheckResultRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;

    @Transactional
    public void saveHealthCheckResult(HealthCheckResult healthCheckResult, MonitoredService service) {
        healthCheckResultRepository.save(healthCheckResult);

        service.setLastCheckedAt(Instant.now());
        monitoredServiceRepository.save(service);
    }

    public HealthResponse getHealthResponses(long serviceId, String from, String to) {
        Instant fromInstant = Instant.parse(from);
        Instant toInstant = Instant.parse(to);

        List<HealthCheckResult> results = healthCheckResultRepository.getCheckResultHistory(
                serviceId,
                fromInstant,
                toInstant);

        double uptimePercentage = calculateUptimePercentage(results);
        double responseTimeAvg = calculateResponseTimeAvg(results);
        String serviceName = monitoredServiceRepository.findById(serviceId).map(MonitoredService::getName).orElse("Unknown");

        List<HealthResponse.CheckItem> checkItems = results.stream()
                .map(result -> new HealthResponse.CheckItem(
                        result.getCheckedAt().toString(),
                        result.getStatus().name(),
                        result.getResponseTimeMs()))
                .sorted(Comparator.comparing(HealthResponse.CheckItem::checkedAt).reversed())
                .limit(50)
                .toList();

        return new HealthResponse(
                serviceId,
                serviceName,
                from,
                to,
                uptimePercentage,
                responseTimeAvg,
                checkItems);
    }

    private double calculateResponseTimeAvg(List<HealthCheckResult> results) {
        return results.stream()
                .mapToDouble(HealthCheckResult::getResponseTimeMs)
                .average()
                .orElse(0);
    }

    private double calculateUptimePercentage(List<HealthCheckResult> results) {
        long totalChecks = results.size();
        long healthyChecks = results.stream().filter(item -> item.getStatus().equals(HealthStatus.UP)).count();
        return (double) healthyChecks / totalChecks * 100;
    }
}
