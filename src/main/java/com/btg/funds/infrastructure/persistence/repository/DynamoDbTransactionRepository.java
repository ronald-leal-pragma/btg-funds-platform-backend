package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.item.TransactionItem;
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

import java.time.Instant;
import java.util.List;

@Slf4j
@Profile("aws")
@Repository
public class DynamoDbTransactionRepository implements TransactionRepository {

    private static final String CLIENT_ID = "1";

    private final DynamoDbTable<TransactionItem> table;

    public DynamoDbTransactionRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.transactions-table:Transactions}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(TransactionItem.class));
    }

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("[REPO] DynamoDbTransactionRepository.save: id={}", transaction.id());
        table.putItem(toItem(transaction));
        return transaction;
    }

    @Override
    public List<Transaction> findAll() {
        return findAllSortedByTimestampDesc();
    }

    @Override
    public List<Transaction> findAllSortedByTimestampDesc() {
        log.debug("[REPO] DynamoDbTransactionRepository.findAllSortedByTimestampDesc");
        var request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(CLIENT_ID).build()))
                .scanIndexForward(false)   // DESC por SK (timestamp ISO-8601)
                .build();

        return table.query(request)
                .items()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // ---------------------------------------------------------

    private TransactionItem toItem(Transaction t) {
        return new TransactionItem(
                CLIENT_ID,
                t.timestamp().toString(),   // ISO-8601 como SK
                t.id(),
                t.type().name(),
                t.fundId(),
                t.fundName(),
                t.amount()
        );
    }

    private Transaction toDomain(TransactionItem item) {
        return new Transaction(
                item.getTransactionId(),
                Transaction.TransactionType.valueOf(item.getType()),
                item.getFundId(),
                item.getFundName(),
                item.getAmount(),
                Instant.parse(item.getTimestamp())
        );
    }
}
