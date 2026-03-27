package com.btg.funds.infrastructure.persistence.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TransactionItem {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String clientId;

    @Getter(onMethod_ = {@DynamoDbSortKey})
    private String timestamp;

    @Getter
    private String transactionId;

    @Getter
    private String type;

    @Getter
    private String fundId;

    @Getter
    private String fundName;

    @Getter
    private long amount;
}
