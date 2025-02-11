package com.example.paymentProcess.repository;

import com.example.paymentProcess.entity.StpConfigurations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StpConfigurationRepository extends MongoRepository<StpConfigurations, String> {

    List<StpConfigurations> findByCustomerName(String customerName);
}
