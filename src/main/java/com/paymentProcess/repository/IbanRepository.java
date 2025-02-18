package com.paymentProcess.repository;

import com.paymentProcess.entity.IbanTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IbanRepository extends MongoRepository<IbanTable, String> {
    boolean existsByBicAndIban(String debtorBic, String debtorIban);

    Optional<IbanTable> findByBicAndIbanAndName(String bic, String iban, String name);

    List<String> findBicByIbanAndName(String iban, String name);
}
