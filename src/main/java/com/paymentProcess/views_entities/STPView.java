package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "stpViews")
@Data
public class STPView {
    private int totalTransactions;
    private int stpTransactions;
    private BigDecimal stpAmount;
    private double stpPercentage;

}
