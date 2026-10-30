package llc.devlab.app.service;

import llc.devlab.app.exception.ValidationException;
import llc.devlab.app.model.dto.HealthAggregateData;
import llc.devlab.app.model.dto.HealthResponse;
import llc.devlab.app.model.entity.HealthCheckResult;
import llc.devlab.app.model.entity.MonitoredService;
import llc.devlab.app.repository.HealthCheckResultRepository;
import llc.devlab.app.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;
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
        Instant fromInstant = parse(from);
        Instant toInstant = parse(to);

        HealthAggregateData aggregateData = healthCheckResultRepository.getHealthAggregateData(
                serviceId,
                fromInstant,
                toInstant);

        List<HealthCheckResult> results = healthCheckResultRepository.getCheckResultHistory(
                serviceId,
                fromInstant,
                toInstant);

        String serviceName = monitoredServiceRepository.findById(serviceId).map(MonitoredService::getName).orElse("Unknown");

        List<HealthResponse.CheckItem> checkItems = results.stream()
                .map(result -> new HealthResponse.CheckItem(
                        result.getCheckedAt().toString(),
                        result.getStatus().name(),
                        result.getResponseTimeMs()))
                .sorted(Comparator.comparing(HealthResponse.CheckItem::checkedAt).reversed())
                .toList();

        return new HealthResponse(
                serviceId,
                serviceName,
                from,
                to,
                aggregateData.uptimePercentage(),
                aggregateData.responseTimeAvg(),
                checkItems);
    }

    private Instant parse(String instantString) {
        try {
            return Instant.parse(instantString);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Invalid instant string format: " + instantString);
        }
    }
}
