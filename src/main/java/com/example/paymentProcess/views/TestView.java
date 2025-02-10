package com.example.paymentProcess.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TestView")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestView {

    private int totalCount;
    private double totalAmount;
}
