package com.example.paymentProcess.repository;

import com.example.paymentProcess.entity.AutoCorrectRules;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoCorrectedRulesRepository extends MongoRepository<AutoCorrectRules, String> {
    boolean existsByTypeAndMessageInfoId(String dbtr, String msgId);

    AutoCorrectRules findByTypeAndMessageInfoId(String dbtr, String msgId);
}
