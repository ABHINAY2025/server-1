package com.paymentProcess.repository.rulesSuggestion;

import com.paymentProcess.entity.ruleSuggestion.StageCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageCollectionRepository extends MongoRepository<StageCollection, String> {
    List<StageCollection> findBy_idIn(List<String> ids);

    List<StageCollection> findByRulesStatus(String analysed);
}
