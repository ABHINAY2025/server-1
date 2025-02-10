package com.example.paymentProcess.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "networks")
@Data
public class Networks {

    private String _id;
    private boolean isApproved;
    private String name;
    private String status;
    private String additionalInfo;
    private String type;
    private String companyId;
    private String companyName;
    private String BIC;
    private String country;
    private String city;
}
