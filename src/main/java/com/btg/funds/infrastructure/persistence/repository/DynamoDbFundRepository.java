package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.infrastructure.persistence.item.FundItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;

@Slf4j
@Profile("aws")
@Repository
public class DynamoDbFundRepository implements FundRepository {

    private final DynamoDbTable<FundItem> table;

    public DynamoDbFundRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.funds-table:Funds}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(FundItem.class));
    }

    @Override
    public List<Fund> findAll() {
        log.debug("[REPO] DynamoDbFundRepository.findAll");
        return table.scan().items().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Fund> findById(String id) {
        log.debug("[REPO] DynamoDbFundRepository.findById: id={}", id);
        var key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(table.getItem(key)).map(this::toDomain);
    }

    @Override
    public Fund save(Fund fund) {
        log.debug("[REPO] DynamoDbFundRepository.save: id={}", fund.id());
        table.putItem(toItem(fund));
        return fund;
    }

    // ---------------------------------------------------------

    private FundItem toItem(Fund fund) {
        return new FundItem(fund.id(), fund.name(), fund.minAmount(), fund.category());
    }

    private Fund toDomain(FundItem item) {
        return new Fund(item.getId(), item.getName(), item.getMinAmount(), item.getCategory());
    }
}
