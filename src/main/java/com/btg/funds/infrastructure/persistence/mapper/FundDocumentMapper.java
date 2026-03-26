package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.infrastructure.persistence.document.FundDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FundDocumentMapper {

    Fund toDomain(FundDocument doc);

    FundDocument toDocument(Fund fund);
}
