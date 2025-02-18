package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.RepairView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairViewRepo extends MongoRepository<RepairView, String> {
}
