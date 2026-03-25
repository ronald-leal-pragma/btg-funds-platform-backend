package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.infrastructure.persistence.document.FundDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoFundRepository implements FundRepository {

    private final SpringFundRepository spring;

    @Override
    public List<Fund> findAll() {
        return spring.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Fund> findById(String id) {
        return spring.findById(id).map(this::toDomain);
    }

    private Fund toDomain(FundDocument doc) {
        return new Fund(doc.getId(), doc.getName(), doc.getMinAmount(), doc.getCategory());
    }
}
