package com.example.paymentProcess.dto;

import com.example.paymentProcess.entity.Networks;
import lombok.Data;

@Data
public class SwitchNetworkRequest {

    private Boolean isApproved;
    private Networks secondaryNetwork;
}
