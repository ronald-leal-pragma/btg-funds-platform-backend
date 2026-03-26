package com.btg.funds.application.port.in;

import com.btg.funds.domain.model.Transaction;

public interface CancelFundPort {
    Transaction execute(String fundId);
}
