package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.infrastructure.persistence.mapper.FundDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoFundRepository implements FundRepository {

    private final SpringFundRepository spring;
    private final FundDocumentMapper mapper;

    @Override
    public List<Fund> findAll() {
        log.debug("[REPO] MongoFundRepository - findAll");
        return spring.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Fund> findById(String id) {
        log.debug("[REPO] MongoFundRepository - findById: id={}", id);
        return spring.findById(id).map(mapper::toDomain);
    }
}
