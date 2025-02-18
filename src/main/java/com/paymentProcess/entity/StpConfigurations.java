package com.paymentProcess.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "stpconfigurations")
@Data
public class StpConfigurations {

    @Setter
    @Getter
    private String _id;
    private List<Object> when;
    private List<Object> then;
    private Boolean isActive;
    private String customerName;
    private String networkName;
    private String ruleName;
    private String ruleType;
    private String ruleSubType;
    private String ruleDescription;
    private LocalDateTime selectedStartDate;
    private LocalDateTime selectedEndDate;



}
