package com.example.paymentProcess.views_entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "autocorrectView")
@Data
public class AutocorrectedView {
    private int totalTransactions;
    private int autocorrectedTransactions;
    private BigDecimal autocorrectedAmount;
    private double autocorrectedPercentage;

}
