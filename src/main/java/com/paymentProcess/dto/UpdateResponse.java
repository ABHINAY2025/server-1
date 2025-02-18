package com.paymentProcess.dto;

import com.paymentProcess.entity.Networks;
import lombok.Data;

@Data
public class UpdateResponse {
    private String message;
    private Networks network;
}
