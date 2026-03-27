package com.btg.funds.application.port.in;

import com.btg.funds.domain.model.Transaction;

public interface SubscribeFundPort {
    Transaction execute(String clientId, String fundId);
}
