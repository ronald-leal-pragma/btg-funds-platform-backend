package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionDocumentMapper {

    @Mapping(target = "type", expression = "java(Transaction.TransactionType.valueOf(doc.getType()))")
    Transaction toDomain(TransactionDocument doc);

    @Mapping(target = "type", expression = "java(transaction.type().name())")
    TransactionDocument toDocument(Transaction transaction);
}
