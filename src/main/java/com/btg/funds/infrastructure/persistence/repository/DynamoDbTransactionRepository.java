package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.item.TransactionItem;
import com.btg.funds.infrastructure.persistence.mapper.TransactionItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Slf4j
@Profile("aws")
@Repository
public class DynamoDbTransactionRepository implements TransactionRepository {

    private final DynamoDbTable<TransactionItem> table;
    private final TransactionItemMapper mapper;

    public DynamoDbTransactionRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.transactions-table:Transactions}") String tableName,
            TransactionItemMapper mapper) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(TransactionItem.class));
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("[REPO] DynamoDbTransactionRepository.save: id={}", transaction.id());
        table.putItem(mapper.toItem(transaction));
        return transaction;
    }

    @Override
    public List<Transaction> findByClientId(String clientId) {
        return findByClientIdSortedByTimestampDesc(clientId);
    }

    @Override
    public List<Transaction> findByClientIdSortedByTimestampDesc(String clientId) {
        log.debug("[REPO] DynamoDbTransactionRepository.findByClientIdSortedByTimestampDesc: clientId={}", clientId);
        var request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(clientId).build()))
                .scanIndexForward(false)
                .build();

        return table.query(request)
                .items()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
