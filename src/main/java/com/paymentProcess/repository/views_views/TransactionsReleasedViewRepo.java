package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.TransactionsReleasedView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsReleasedViewRepo extends MongoRepository<TransactionsReleasedView, String> {
}
