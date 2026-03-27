package com.btg.funds.application.port.in;

import com.btg.funds.application.dto.CreateClientRequest;
import com.btg.funds.domain.model.Client;

public interface CreateClientPort {
    Client execute(CreateClientRequest request);
}
