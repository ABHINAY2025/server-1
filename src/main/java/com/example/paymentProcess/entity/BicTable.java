package com.example.paymentProcess.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bictables")
@Data
public class BicTable {

    private String _id;
    private String bic;
    private String address;
}
