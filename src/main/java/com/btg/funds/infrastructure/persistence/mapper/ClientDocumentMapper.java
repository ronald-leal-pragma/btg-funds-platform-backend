package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Client;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientDocumentMapper {

    @Mapping(
        target = "activeFundIds",
        expression = "java(doc.getActiveFundIds() != null ? doc.getActiveFundIds() : new java.util.ArrayList<>())"
    )
    Client toDomain(ClientDocument doc);

    ClientDocument toDocument(Client client);
}
