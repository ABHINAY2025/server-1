package com.paymentProcess.repository;

import com.paymentProcess.entity.IsoStandardFields;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsoStandardFieldsRepository extends MongoRepository<IsoStandardFields, String> {
}
