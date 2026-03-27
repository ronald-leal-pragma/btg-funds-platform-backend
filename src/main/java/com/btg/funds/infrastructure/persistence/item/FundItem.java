package com.btg.funds.infrastructure.persistence.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FundItem {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String id;

    @Getter
    private String name;

    @Getter
    private long minAmount;

    @Getter
    private String category;
}
