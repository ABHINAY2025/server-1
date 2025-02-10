package com.example.paymentProcess.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ibantables")
@Data
public class IbanTable {

    private String _id;
    private String bic;
    private String name;
    private String iban;
}
