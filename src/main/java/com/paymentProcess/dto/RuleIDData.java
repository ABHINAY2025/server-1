package com.paymentProcess.dto;


import com.paymentProcess.entity.PaymentFile;
import lombok.Data;

import java.util.ArrayList;
@Data
public class RuleIDData {
    private ArrayList<PaymentFile.RuleID> ruleIDs;

    @Data
    public static class RuleID {
        private String type;
        private ArrayList<String> bank_rules;
        private ArrayList<String> customer_rules;
    }

}
