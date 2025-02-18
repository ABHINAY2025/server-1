package com.paymentProcess.repository.rulesSuggestion;

import com.paymentProcess.entity.ruleSuggestion.MasterCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends MongoRepository<MasterCollection, String> {
}
