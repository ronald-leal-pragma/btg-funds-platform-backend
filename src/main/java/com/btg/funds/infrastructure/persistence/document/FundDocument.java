package com.btg.funds.infrastructure.persistence.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "funds")
public class FundDocument {

    @Id
    private String id;

    private String name;

    @Field("min_amount")
    private long minAmount;

    private String category;

    public FundDocument() {}

    private FundDocument(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.minAmount = builder.minAmount;
        this.category = builder.category;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private long minAmount;
        private String category;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder minAmount(long minAmount) {
            this.minAmount = minAmount;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public FundDocument build() {
            return new FundDocument(this);
        }
    }
}
