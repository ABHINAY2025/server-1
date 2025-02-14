package com.example.paymentProcess.dto;

import com.example.paymentProcess.entity.Networks;
import lombok.Data;

@Data
public class UpdateResponse {
    private String message;
    private Networks network;
}
