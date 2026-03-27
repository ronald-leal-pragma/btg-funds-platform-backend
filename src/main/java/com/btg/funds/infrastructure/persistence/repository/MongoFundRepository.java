package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.infrastructure.persistence.mapper.FundDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Profile("!aws")
@Repository
@RequiredArgsConstructor
public class MongoFundRepository implements FundRepository {

    private final SpringFundRepository springRepository;
    private final FundDocumentMapper mapper;

    @Override
    public List<Fund> findAll() {
        log.debug("[REPO] MongoFundRepository.findAll");
        return springRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Fund> findById(String id) {
        log.debug("[REPO] MongoFundRepository.findById: id={}", id);
        return springRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Fund save(Fund fund) {
        log.debug("[REPO] MongoFundRepository.save: id={}", fund.id());
        return mapper.toDomain(springRepository.save(mapper.toDocument(fund)));
    }
}
