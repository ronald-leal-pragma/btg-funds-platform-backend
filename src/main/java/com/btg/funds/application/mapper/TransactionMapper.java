package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.TransactionResponse;
import com.btg.funds.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "type", expression = "java(transaction.type().name())")
    TransactionResponse toResponse(Transaction transaction);
}
