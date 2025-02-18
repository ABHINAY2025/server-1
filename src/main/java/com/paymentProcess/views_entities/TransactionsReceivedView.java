package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "TransReceivedView")
@Data
public class TransactionsReceivedView {
    private int totalTransactions;
    private BigDecimal transactionReceivedAmount;
    private double percentage;

}
