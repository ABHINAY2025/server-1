package com.example.paymentProcess.repository;

import com.example.paymentProcess.entity.Networks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworksRepository extends MongoRepository<Networks, String> {
    Networks findByName(String name);
}
