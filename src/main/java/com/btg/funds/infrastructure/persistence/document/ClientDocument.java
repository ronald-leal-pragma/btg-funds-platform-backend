package com.btg.funds.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Profile("!aws")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "client")
public class ClientDocument {

    @Id
    private String id;

    private long balance;

    @Field("notification_preference")
    private String notificationPreference;

    @Field("contact_info")
    private String contactInfo;

    @Field("active_fund_ids")
    private List<String> activeFundIds;
}
