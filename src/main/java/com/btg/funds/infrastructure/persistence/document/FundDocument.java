package com.btg.funds.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Profile("!aws")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "funds")
public class FundDocument {

    @Id
    private String id;

    private String name;

    @Field("min_amount")
    private long minAmount;

    private String category;
}
