package com.example.paymentProcess.repository;

import com.example.paymentProcess.entity.Companies;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompaniesRepository extends MongoRepository<Companies, String> {
}
