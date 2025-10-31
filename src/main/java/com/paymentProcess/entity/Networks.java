package com.paymentProcess.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "networks")
@Data
public class Networks {

    @Id
    private String id;

    private String networkRuleName;        // Rule name
    private String fileType;               // File type (ISO, XML, etc.)
    private String networkRuleType;        // Type of rule
    private String networkRuleSubType;     // Sub-type of rule
    private String networkRuleDescription; // Description of rule

    private LocalDateTime networkSelectedStartDate; // Start date
    private LocalDateTime networkSelectedEndDate;   // End date

    private List<WhenCondition> networkWhen;  // Array of conditions
    private List<ThenAction> networkThen;     // Array of actions

    private String name;
    private String type;
    private String status;
    private String additionalInfo;

    private boolean approved;  // Approval flag

    // ---------------- Inner Classes ----------------
    @Data
    public static class WhenCondition {
        private String field;     // Field name
        private String operator;  // Operator (equals, greaterThan, etc.)
        private String value;     // Value to compare
    }

    @Data
    public static class ThenAction {
        private String field;  // Field to modify
        private String value;  // Value to set
    }
}
