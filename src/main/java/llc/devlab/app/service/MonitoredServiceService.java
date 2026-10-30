package llc.devlab.app.service;

import llc.devlab.app.exception.ResourceNotFoundException;
import llc.devlab.app.mapper.ServiceMapper;
import llc.devlab.app.model.dto.CreateServiceRequest;
import llc.devlab.app.model.dto.ServiceResponse;
import llc.devlab.app.model.dto.UpdateServiceRequest;
import llc.devlab.app.model.entity.MonitoredService;
import llc.devlab.app.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoredServiceService {
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        MonitoredService entity = serviceMapper.toEntity(request);
        monitoredServiceRepository.save(entity);
        return serviceMapper.toDto(entity);
    }

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
        monitoredServiceRepository.save(entity);
        return serviceMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        MonitoredService entity = monitoredServiceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Monitored service not found with id: " + id));
        monitoredServiceRepository.delete(entity);
    }

    public List<ServiceResponse> getAll() {
        return serviceMapper.toDtoList(monitoredServiceRepository.findAll());
    }

    public List<MonitoredService> findServicesDueForCheck() {
        return monitoredServiceRepository.findServicesDueForCheck();
    }
}
