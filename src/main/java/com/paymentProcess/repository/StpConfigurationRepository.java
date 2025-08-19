package com.paymentProcess.repository;

import com.paymentProcess.entity.BankRules;
import com.paymentProcess.entity.StpConfigurations;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StpConfigurationRepository extends MongoRepository<StpConfigurations, String> {

    List<StpConfigurations> findByCustomerName(String customerName);

    List<StpConfigurations> findByIdIn(List<ObjectId> ids);
}
