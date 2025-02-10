package com.example.paymentProcess.repository.views_views;

import com.example.paymentProcess.views_entities.TransactionsReceivedView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsReceivedViewRepo extends MongoRepository<TransactionsReceivedView, String> {
}
