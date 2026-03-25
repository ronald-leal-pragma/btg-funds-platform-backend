package com.btg.funds.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "clients")
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
