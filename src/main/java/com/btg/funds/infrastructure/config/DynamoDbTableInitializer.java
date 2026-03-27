package com.btg.funds.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/**
 * Crea las tablas DynamoDB al arrancar si no existen.
 * - Local: DynamoDB Local no tiene tablas predefinidas → las crea.
 * - AWS: CloudFormation ya las creó → ResourceInUseException se ignora.
 *
 * @Order(0) garantiza que se ejecuta antes que DataSeeder (@Order(1)).
 */
@Slf4j
@Profile("local")
@Component
@Order(0)
@RequiredArgsConstructor
public class DynamoDbTableInitializer implements CommandLineRunner {

    private final DynamoDbClient dynamoDbClient;

    @Value("${aws.dynamodb.clients-table:Clients}")
    private String clientsTable;

    @Value("${aws.dynamodb.funds-table:Funds}")
    private String fundsTable;

    @Value("${aws.dynamodb.transactions-table:Transactions}")
    private String transactionsTable;

    @Override
    public void run(String... args) {
        createSingleKeyTable(clientsTable, "id");
        createSingleKeyTable(fundsTable, "id");
        createCompositeKeyTable(transactionsTable, "clientId", "timestamp");
    }

    /** Tabla con solo PK (sin SK). */
    private void createSingleKeyTable(String tableName, String pkName) {
        try {
            dynamoDbClient.createTable(b -> b
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(pkName)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(pkName)
                            .keyType(KeyType.HASH)
                            .build()));
            log.info("[DYNAMODB] Tabla '{}' creada", tableName);
        } catch (DynamoDbException e) {
            log.debug("[DYNAMODB] Tabla '{}' ya existe o sin permiso para crearla: {}", tableName, e.getMessage());
        }
    }

    /** Tabla con PK + SK. */
    private void createCompositeKeyTable(String tableName, String pkName, String skName) {
        try {
            dynamoDbClient.createTable(b -> b
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName(pkName).attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName(skName).attributeType(ScalarAttributeType.S).build())
                    .keySchema(
                            KeySchemaElement.builder().attributeName(pkName).keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName(skName).keyType(KeyType.RANGE).build()));
            log.info("[DYNAMODB] Tabla '{}' creada (PK={}, SK={})", tableName, pkName, skName);
        } catch (DynamoDbException e) {
            log.debug("[DYNAMODB] Tabla '{}' ya existe o sin permiso para crearla: {}", tableName, e.getMessage());
        }
    }
}
