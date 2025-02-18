package com.paymentProcess.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Companies {

    private String _id;
    private List<String> subsidiaries;
    private String name;
    private Object address;
    private Object contact;
    private long founded;
    private int employees;
    private long revenue;
    private String industry;
    private String website;
    private String ceo;
    private long marketCap;



    @Data
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zip;

    }

    @Data
    public static class Contact {
        private String phone;
        private String email;

    }
}
