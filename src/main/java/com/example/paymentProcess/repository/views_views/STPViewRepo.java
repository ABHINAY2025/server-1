package com.example.paymentProcess.repository.views_views;

import com.example.paymentProcess.views_entities.STPView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface STPViewRepo extends MongoRepository<STPView, Integer> {
}
