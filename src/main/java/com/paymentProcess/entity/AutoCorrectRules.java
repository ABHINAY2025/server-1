package com.paymentProcess.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "autocorrectrules")
@Data
public class AutoCorrectRules {

    private String _id;
    private String type;
    private String messageInfoId;
    private AcctDetails AcctDetails;
    private AcctDetails AcctDetails_Fix;

    @Data
    public static class AcctDetails {
        private String IBAN;
        private String Name;
        private String BIC;
        private String BankName;
        private String category;
    }
}
