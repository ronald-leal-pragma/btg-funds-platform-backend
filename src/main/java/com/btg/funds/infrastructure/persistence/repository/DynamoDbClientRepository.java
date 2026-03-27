package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.item.ClientItem;
import com.btg.funds.infrastructure.persistence.mapper.ClientItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Profile("aws")
@Repository
public class DynamoDbClientRepository implements ClientRepository {

    private final DynamoDbTable<ClientItem> table;
    private final ClientItemMapper mapper;

    public DynamoDbClientRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.clients-table:Clients}") String tableName,
            ClientItemMapper mapper) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ClientItem.class));
        this.mapper = mapper;
    }

    @Override
    public Optional<Client> findById(String id) {
        log.debug("[REPO] DynamoDbClientRepository.findById: id={}", id);
        var key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(table.getItem(key)).map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        log.debug("[REPO] DynamoDbClientRepository.findByEmail: email={}", email);
        var scanRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("email = :email")
                        .expressionValues(Map.of(":email", AttributeValue.fromS(email)))
                        .build())
                .build();
        return table.scan(scanRequest).items().stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public Client save(Client client) {
        log.debug("[REPO] DynamoDbClientRepository.save: id={}", client.id());
        table.putItem(mapper.toItem(client));
        return client;
    }
}
