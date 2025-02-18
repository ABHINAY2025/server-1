package com.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "approveView")
@Data
public class ApprovedView {
    private int totalTransactions;
    private BigDecimal totalAmount;
    private int approvedTransactions;
    private double approvedAmount;
    private double approvedPercentage;

}
