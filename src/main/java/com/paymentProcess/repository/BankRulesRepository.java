package com.paymentProcess.repository;

import com.paymentProcess.entity.BankRules;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRulesRepository extends MongoRepository<BankRules, String> {
    List<BankRules> findByBankName(String name);

    List<BankRules> findByRuleType(String dbtr);

    List<BankRules> findByIdIn(List<ObjectId> ids);


}
