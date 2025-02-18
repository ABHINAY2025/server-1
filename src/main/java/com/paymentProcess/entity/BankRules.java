package com.paymentProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Document(collection = "bankrules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankRules {


    private String _id;
    private String bankName;
    private List<Object> when;
    private List<Object> then;
    private Boolean isActive;
    private String networkName;
    private String ruleName;
    private String ruleType;
    private String ruleSubType;
    private String ruleDescription;
    private LocalDateTime selectedStartDate;
    private LocalDateTime selectedEndDate;

}
