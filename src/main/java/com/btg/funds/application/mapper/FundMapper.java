package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.FundResponse;
import com.btg.funds.domain.model.Fund;
import org.springframework.stereotype.Component;

@Component
public class FundMapper {

    public FundResponse toResponse(Fund fund) {
        return new FundResponse(
                fund.id(),
                fund.name(),
                fund.minAmount(),
                fund.category()
        );
    }
}
