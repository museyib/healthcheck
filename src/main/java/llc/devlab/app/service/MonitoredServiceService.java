package llc.devlab.app.service;

import llc.devlab.app.exception.ResourceNotFoundException;
import llc.devlab.app.exception.ValidationException;
import llc.devlab.app.mapper.ServiceMapper;
import llc.devlab.app.model.dto.CreateServiceRequest;
import llc.devlab.app.model.dto.ServiceResponse;
import llc.devlab.app.model.dto.UpdateServiceRequest;
import llc.devlab.app.model.dto.ServiceDependencyGraph;
import llc.devlab.app.model.entity.MonitoredService;
import llc.devlab.app.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoredServiceService {
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        MonitoredService entity = serviceMapper.toEntity(request);
        entity.setDependencies(resolveDependencies(request.dependsOn(), null));
        monitoredServiceRepository.save(entity);
        return serviceMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ServiceResponse getById(Long id) {
        MonitoredService entity = monitoredServiceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Monitored service not found with id: " + id));
        return serviceMapper.toDto(entity);
    }

    @Transactional
    public ServiceResponse update(UpdateServiceRequest request) {
        MonitoredService entity = monitoredServiceRepository.findById(request.id()).orElseThrow(
                () -> new ResourceNotFoundException("Monitored service not found with id: " + request.id()));
        serviceMapper.updateEntity(request, entity);
        if (request.dependsOn() != null) {
            entity.setDependencies(resolveDependencies(request.dependsOn(), entity.getId()));
        }
        monitoredServiceRepository.save(entity);
        return serviceMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        MonitoredService entity = monitoredServiceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Monitored service not found with id: " + id));
        monitoredServiceRepository.findAll().stream()
                .filter(service -> service.getDependencies().remove(entity))
                .forEach(monitoredServiceRepository::save);
        monitoredServiceRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getAll() {
        return serviceMapper.toDtoList(monitoredServiceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ServiceDependencyGraph getDependencyGraph() {
        List<MonitoredService> services = monitoredServiceRepository.findAll();
        List<ServiceDependencyGraph.Node> nodes = services.stream()
                .map(service -> new ServiceDependencyGraph.Node(service.getId(), service.getName())).toList();
        List<ServiceDependencyGraph.Edge> edges = services.stream()
                .flatMap(service -> service.getDependencies().stream()
                        .map(dependency -> new ServiceDependencyGraph.Edge(service.getId(), dependency.getId())))
                .toList();
        return new ServiceDependencyGraph(nodes, edges);
    }

    private Set<MonitoredService> resolveDependencies(Set<Long> dependencyIds, Long serviceId) {
        if (dependencyIds == null || dependencyIds.isEmpty()) return Set.of();
        if (serviceId != null && dependencyIds.contains(serviceId)) {
            throw new ValidationException("A service cannot depend on itself");
        }
        List<MonitoredService> dependencies = monitoredServiceRepository.findAllById(dependencyIds);
        if (dependencies.size() != dependencyIds.size()) {
            Set<Long> foundIds = dependencies.stream().map(MonitoredService::getId).collect(Collectors.toSet());
            Set<Long> missingIds = dependencyIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new ResourceNotFoundException("Dependencies not found with ids: " + missingIds);
        }
        return Set.copyOf(dependencies);
    }

    public List<MonitoredService> findServicesDueForCheck() {
        return monitoredServiceRepository.findServicesDueForCheck();
    }
}
