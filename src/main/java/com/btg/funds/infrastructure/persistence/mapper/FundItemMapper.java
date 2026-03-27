package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.infrastructure.persistence.item.FundItem;
import org.springframework.stereotype.Component;

@Component
public class FundItemMapper {

    public FundItem toItem(Fund fund) {
        return new FundItem(fund.id(), fund.name(), fund.minAmount(), fund.category());
    }

    public Fund toDomain(FundItem item) {
        return new Fund(item.getId(), item.getName(), item.getMinAmount(), item.getCategory());
    }
}
