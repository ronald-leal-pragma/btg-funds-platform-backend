package com.btg.funds.domain.repository;

import com.btg.funds.domain.model.Fund;

import java.util.List;
import java.util.Optional;

public interface FundRepository {
    List<Fund> findAll();
    Optional<Fund> findById(String id);
}
