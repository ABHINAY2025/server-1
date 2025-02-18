package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.TransactionValueView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionValueViewRepo extends MongoRepository<TransactionValueView, String> {
}
