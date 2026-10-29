package llc.devlab.app.mapper;

import llc.devlab.app.model.dto.CreateServiceRequest;
import llc.devlab.app.model.dto.ServiceResponse;
import llc.devlab.app.model.dto.UpdateServiceRequest;
import llc.devlab.app.model.entity.MonitoredService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceResponse toDto(MonitoredService entity);

    MonitoredService toEntity(CreateServiceRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntity(UpdateServiceRequest dto, @MappingTarget MonitoredService entity);

    List<ServiceResponse> toDtoList(List<MonitoredService> all);
}
