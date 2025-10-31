package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.OnHoldView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnHoldViewRepo extends MongoRepository<OnHoldView, Integer> {

}
