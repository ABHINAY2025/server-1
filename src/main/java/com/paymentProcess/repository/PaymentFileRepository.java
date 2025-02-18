package com.paymentProcess.repository;

import com.paymentProcess.entity.PaymentFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentFileRepository extends MongoRepository<PaymentFile, String> {
}
