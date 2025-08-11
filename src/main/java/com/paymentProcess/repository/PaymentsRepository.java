package com.paymentProcess.repository;

import com.paymentProcess.entity.Payments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentsRepository extends MongoRepository<Payments, String> {
    List<Payments> findByFileStatus(String fileStatus);
}


