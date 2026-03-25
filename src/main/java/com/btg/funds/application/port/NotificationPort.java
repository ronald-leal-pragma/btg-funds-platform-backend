package com.btg.funds.application.port;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;

public interface NotificationPort {
    void notifySubscription(Client client, Fund fund);
}
