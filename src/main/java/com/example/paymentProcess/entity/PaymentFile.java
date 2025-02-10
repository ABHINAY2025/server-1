package com.example.paymentProcess.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "paymentfiles")
public class PaymentFile {

    private String _id;
    private Boolean sequenceNumber;
    private Boolean isAutoCorrect;
    private Boolean isStp;
    private Boolean isApproved;
    private List<String> notes;
    private ArrayList<RuleID> ruleIDs;
    private Boolean toBeEdited;
    private Boolean active;
    private String originalXml;
    private Object debtorData;
    private Object creditorData;
    private String msgId;
    private BigDecimal ctrlSum;
    private String createdAt;
    private String updatedAt;
    private MlSuggestion cdtrMlSuggestion;
    private MlSuggestion dbtrMlSuggestion;
    private String updatedXml;
    private String fileStatus;
    private String statusID;
    private String substatusID;


    @Getter
    @Setter
    public static class RuleID {
        private String type;  // "debtor" or "creditor"
        private ArrayList<String> bank_rules;  // List of ObjectId references for bank rules
        private ArrayList<String> customer_rules;  // List of ObjectId references for customer rules
    }

    @Getter
    @Setter
    public static class MlSuggestion {
        @JsonProperty("MLScore")
        private Double mlScore;

        @JsonProperty("MLSuggestion")
        private String mlSuggestion;
    }
}
