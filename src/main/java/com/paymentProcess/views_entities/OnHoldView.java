package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "onHoldViews")
@Data
public class OnHoldView {
    private int totalTransactions;
    private BigDecimal totalAmount;
    private int onHoldTransactions;
    private BigDecimal onHoldAmount;
    private double onHoldPercentage;
    private int releasedTransactions;
    private BigDecimal releasedAmount;
    private double releasedPercentage;

}
