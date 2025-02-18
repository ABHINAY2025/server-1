package com.paymentProcess.repository;

import com.paymentProcess.entity.BicTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BicRepository extends MongoRepository<BicTable, String> {
    boolean existsByBicAndAddress(String debtorBic, String debtorAddress);

    Optional<BicTable> findByBicAndAddress(String bic, String address);

    List<String> findBicByAddress(String address);
}
