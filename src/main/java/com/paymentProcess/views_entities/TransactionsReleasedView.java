package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "releasedView")
@Data
public class TransactionsReleasedView {
    private int totalTransactions;
    private int releasedTransactions;
    private BigDecimal releasedAmount;
    private double releasedPercentage;

}
