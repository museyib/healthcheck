package llc.devlab.app.service;

import llc.devlab.app.model.entity.MonitoredService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final HealthService healthService;
    private final MonitoredServiceService monitoredServiceService;

    @Scheduled(fixedRateString = "${monitoring.scheduler.poll-interval:1000}")
    public void checkStatus() {
        List<MonitoredService> monitoredServices = monitoredServiceService.findServicesDueForCheck();
        healthService.checkStatus(monitoredServices);
    }
}
