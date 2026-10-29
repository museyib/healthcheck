package llc.devlab.app.controller;

import llc.devlab.app.model.dto.HealthResponse;
import llc.devlab.app.service.ServiceMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HealthController {

    private final ServiceMonitoringService serviceMonitoringService;

    @RequestMapping("/{id}/history")
    public ResponseEntity<HealthResponse> getHistory(@PathVariable Long id,
                                                     @RequestParam String from,
                                                     @RequestParam String to) {
        return ResponseEntity.ok(serviceMonitoringService.getHealthResponses(id, from, to));
    }
}
