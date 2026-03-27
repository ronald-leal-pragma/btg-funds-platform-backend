package com.btg.funds.application.port.in;

import com.btg.funds.application.dto.LoginRequest;
import com.btg.funds.domain.model.Client;

public interface LoginPort {
    Client execute(LoginRequest request);
}
