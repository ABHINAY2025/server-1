package com.paymentProcess.repository.views_views;

import com.paymentProcess.views_entities.ApprovedView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovedViewRepo extends MongoRepository<ApprovedView, String> {
}
