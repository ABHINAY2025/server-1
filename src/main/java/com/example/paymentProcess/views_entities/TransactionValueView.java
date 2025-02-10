package com.example.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "transactionValueViews")
@Data
public class TransactionValueView {
    private int totalTransactions;
    private BigDecimal totalAmount;
    private int tier1Transactions;
    private BigDecimal tier1Amount;
    private double tier1Percentage;
    private int tier2Transactions;
    private BigDecimal tier2Amount;
    private double tier2Percentage;
    private int tier3Transactions;
    private BigDecimal tier3Amount;
    private double tier3Percentage;

}
