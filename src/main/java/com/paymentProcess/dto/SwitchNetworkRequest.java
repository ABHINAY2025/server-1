package com.paymentProcess.dto;

import com.paymentProcess.entity.Networks;
import lombok.Data;

@Data
public class SwitchNetworkRequest {

    private Boolean isApproved;
    private Networks secondaryNetwork;
}
