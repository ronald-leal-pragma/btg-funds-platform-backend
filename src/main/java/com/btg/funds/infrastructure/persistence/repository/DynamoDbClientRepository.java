package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.item.ClientItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Profile("aws")
@Repository
public class DynamoDbClientRepository implements ClientRepository {

    private final DynamoDbTable<ClientItem> table;

    public DynamoDbClientRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.clients-table:Clients}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ClientItem.class));
    }

    @Override
    public Optional<Client> findById(String id) {
        log.debug("[REPO] DynamoDbClientRepository.findById: id={}", id);
        var key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(table.getItem(key)).map(this::toDomain);
    }

    @Override
    public Client save(Client client) {
        log.debug("[REPO] DynamoDbClientRepository.save: id={}", client.id());
        table.putItem(toItem(client));
        return client;
    }

    // ---------------------------------------------------------

    private ClientItem toItem(Client client) {
        return new ClientItem(
                client.id(),
                client.balance(),
                client.notificationPreference(),
                client.contactInfo(),
                new ArrayList<>(client.activeFundIds())
        );
    }

    private Client toDomain(ClientItem item) {
        return new Client(
                item.getId(),
                item.getBalance(),
                item.getNotificationPreference(),
                item.getContactInfo(),
                item.getActiveFundIds() != null ? new ArrayList<>(item.getActiveFundIds()) : new ArrayList<>()
        );
    }
}
