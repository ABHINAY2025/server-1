package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "repairedView")
@Data
public class RepairView {
    private int totalTransactions;
    private BigDecimal totalAmount;
    private int toBeRepairedTransactions;
    private BigDecimal toBeRepairedAmount;
    private double toBeRepairedPercentage;
    private int autoCorrectedTransactions;
    private BigDecimal autoCorrectedAmount;
    private double autoCorrectedPercentage;
    private int approvedTransactions;
    private BigDecimal approvedAmount;
    private double approvedPercentage;

}
