package com.btg.funds.infrastructure.persistence.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ClientItem {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String id;

    @Getter
    private long balance;

    @Getter
    private String notificationPreference;

    @Getter
    private String contactInfo;

    @Getter
    private List<String> activeFundIds;
}
