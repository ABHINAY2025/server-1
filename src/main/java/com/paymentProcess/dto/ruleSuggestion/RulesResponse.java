package com.paymentProcess.dto.ruleSuggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RulesResponse {

    private List<RuleWrapper> rules;

    @Data
    @AllArgsConstructor
    public static class RuleWrapper {
        private RuleData master;
        private TopSuggRule top_sugg_rule;
    }

    @Data
    @AllArgsConstructor
    public static class RuleData {
        private String _id;
        private List<Condition> when;
        private List<ThenAction> then;
        private boolean isActive;
        private String customerName;
        private String networkName;
        private String ruleName;
        private String ruleType;
        private String ruleSubType;
        private String ruleDescription;
        private String selectedStartDate;
        private int __v;
    }

    @Data
    @AllArgsConstructor
    public static class TopSuggRule {
        private String _id;
        private List<Condition> when;
        private List<ThenAction> then;
        private boolean isActive;
        private String customerName;
        private String networkName;
        private String ruleName;
        private String ruleType;
        private String ruleSubType;
        private String ruleDescription;
        private String selectedStartDate;
        private int __v;
        private double score;
    }

    @Data
    @AllArgsConstructor
    public static class Condition {
        private String ISOWhenField;
        private String ISOWhenOperator;
        private String ISOWhenValue;
    }

    @Data
    @AllArgsConstructor
    public static class ThenAction {
        private String ns0_DbtrAcct_ns0_Id_ns0_Othr_ns0_Id; // Mapping of the fields
    }
}
