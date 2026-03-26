package com.btg.funds.application.port.in;

import com.btg.funds.application.dto.FundWithStatusResponse;

import java.util.List;

public interface GetFundsPort {
    List<FundWithStatusResponse> execute();
}
