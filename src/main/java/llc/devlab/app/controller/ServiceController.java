package llc.devlab.app.controller;

import jakarta.validation.Valid;
import llc.devlab.app.model.dto.CreateServiceRequest;
import llc.devlab.app.model.dto.ServiceResponse;
import llc.devlab.app.model.dto.UpdateServiceRequest;
import llc.devlab.app.model.dto.ServiceDependencyGraph;
import llc.devlab.app.service.MonitoredServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
public class ServiceController {

    private final MonitoredServiceService monitoredServiceService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAll() {
        return ResponseEntity.ok(monitoredServiceService.getAll());
    }

    @GetMapping("/dependency-graph")
    public ResponseEntity<ServiceDependencyGraph> getDependencyGraph() {
        return ResponseEntity.ok(monitoredServiceService.getDependencyGraph());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredServiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(monitoredServiceService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        monitoredServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ServiceResponse> update(@Valid @RequestBody UpdateServiceRequest request) {
        return ResponseEntity.ok(monitoredServiceService.update(request));
    }
}
