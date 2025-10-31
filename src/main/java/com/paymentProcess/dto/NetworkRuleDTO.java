package com.paymentProcess.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NetworkRuleDTO {

    private String networkRuleName;
    private String fileType;
    private String networkRuleType;
    private String networkRuleSubType;
    private String networkRuleDescription;
    private LocalDateTime networkSelectedStartDate;
    private LocalDateTime networkSelectedEndDate;

    private List<WhenCondition> networkWhen;
    private List<ThenAction> networkThen;

    // ----------------- Inner Entities -----------------

    @Data
    public static class WhenCondition {
        private String field;
        private String operator;
        private String value;
    }

    @Data
    public static class ThenAction {
        private String field;
        private String value;
    }
}
