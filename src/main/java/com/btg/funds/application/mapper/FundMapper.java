package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.FundResponse;
import com.btg.funds.domain.model.Fund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FundMapper {

    FundResponse toResponse(Fund fund);
}
