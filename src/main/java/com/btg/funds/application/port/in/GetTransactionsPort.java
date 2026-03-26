package com.btg.funds.application.port.in;

import com.btg.funds.domain.model.Transaction;

import java.util.List;

public interface GetTransactionsPort {
    List<Transaction> execute();
}
