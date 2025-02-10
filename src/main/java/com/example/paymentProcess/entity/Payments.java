package com.example.paymentProcess.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "paymentFile")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payments {

    private String _id;
    private String originalXml;
    private Creditor initialCreditor;
    private Debtor initialDebtor;
    private Creditor creditor;
    private Debtor debtor;
    private List<RuleID> ruleIDs;
    private String msgId;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean toBeEdited;
    private String updatedXml;
    private String fileStatus;
    private Object notes;
    private List<String> subStatus;
    private MlSuggestion cdtrMlSuggestion;
    private MlSuggestion dbtrMlSuggestion;
    private List<STPConfig> stpConfigRules;
    private List<BicIssue> bicIssues;
    private List<AutoCorrected> autoCorrected;

    @Data
    public static class STPConfig {
        private boolean creditorCustomerRule;
        private boolean debtorCustomerRule;
    }

    @Data
    public static class RuleID {
        private String type;
        private List<String> bank_rules;
        private List<String> customer_rules;
    }

    @Getter
    @Setter
    public static class MlSuggestion {
        @JsonProperty("MLScore")
        private Double mlScore;

        @JsonProperty("MLSuggestion")
        private String mlSuggestion;
    }

    @Data
    public static class AutoCorrected{
        private boolean creditorAutoCorrected;
        private boolean debtorAutoCorrected;
    }

    @Data
    public static class BicIssue{
        private boolean creditorBicIssue;
        private boolean debtorBicIssue;
    }

    @Data
    public static class Debtor {
        private String debtorName;
        private String debtorAddress;
        private String debtorBic;
        private String debtorIban;

    }


    @Data
    public static class Creditor {
        private String creditorName;
        private String creditorAddress;
        private String creditorBic;
        private String creditorIban;

    }

}
