package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.TransactionsReceivedView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsReceivedViewRepo extends MongoRepository<TransactionsReceivedView, String> {
}
