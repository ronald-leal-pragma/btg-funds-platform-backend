package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.ClientResponse;
import com.btg.funds.domain.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    ClientResponse toResponse(Client client);
}
