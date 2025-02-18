package com.paymentProcess.views;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "STP-View")
@Data
public class STPView {
    private int totalTransactions;
    private int stpTransactions;
    private BigDecimal stpAmount;
    private double stpPercentage;

}
