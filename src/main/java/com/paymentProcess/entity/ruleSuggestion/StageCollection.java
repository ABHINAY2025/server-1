package com.paymentProcess.entity.ruleSuggestion;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Document(collection = "stage")
@Data
public class StageCollection {

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
    private String rulesStatus;
}
