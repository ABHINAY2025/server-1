package com.paymentProcess.repository.rulesSuggestion;

import com.paymentProcess.entity.ruleSuggestion.SourceExportCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceExportCollectionRepository extends MongoRepository<SourceExportCollection, String> {
}
