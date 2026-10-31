package llc.devlab.app.mapper;

import llc.devlab.app.model.dto.CreateServiceRequest;
import llc.devlab.app.model.dto.ServiceResponse;
import llc.devlab.app.model.dto.UpdateServiceRequest;
import llc.devlab.app.model.entity.MonitoredService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "dependsOn", expression = "java(entity.getDependencies().stream().map(MonitoredService::getId).collect(java.util.stream.Collectors.toSet()))")
    ServiceResponse toDto(MonitoredService entity);

    @Mapping(target = "dependencies", ignore = true)
    MonitoredService toEntity(CreateServiceRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "dependencies", ignore = true)
    void updateEntity(UpdateServiceRequest dto, @MappingTarget MonitoredService entity);

    List<ServiceResponse> toDtoList(List<MonitoredService> all);
}
