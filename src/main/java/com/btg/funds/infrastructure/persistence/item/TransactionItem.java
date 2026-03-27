package com.btg.funds.infrastructure.persistence.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Item DynamoDB para transacciones.
 * PK: clientId ("1" — cliente único)
 * SK: timestamp (ISO-8601) — permite ordenar DESC con scanIndexForward=false
 * transactionId: UUID de la transacción (atributo regular)
 */
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TransactionItem {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String clientId;

    @Getter(onMethod_ = {@DynamoDbSortKey})
    private String timestamp;   // ISO-8601 string para ordenamiento lexicográfico

    @Getter
    private String transactionId;   // UUID

    @Getter
    private String type;            // APERTURA | CANCELACION

    @Getter
    private String fundId;

    @Getter
    private String fundName;

    @Getter
    private long amount;
}
