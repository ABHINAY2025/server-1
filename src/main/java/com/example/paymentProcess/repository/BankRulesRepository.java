package com.example.paymentProcess.repository;

import com.example.paymentProcess.entity.BankRules;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRulesRepository extends MongoRepository<BankRules, String> {
    List<BankRules> findByBankName(String name);

    List<BankRules> findByRuleType(String dbtr);
}
