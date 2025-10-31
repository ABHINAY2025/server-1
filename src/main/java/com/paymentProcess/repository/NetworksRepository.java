package com.paymentProcess.repository;

import com.paymentProcess.entity.Networks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworksRepository extends MongoRepository<Networks, String> {
    Networks findByName(String name);

//    List<Networks> findByCompanyName(String companyName);

    List<Networks> findByType(String type);

}
