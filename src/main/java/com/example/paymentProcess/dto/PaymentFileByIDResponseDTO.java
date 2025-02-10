package com.example.paymentProcess.dto;

import com.example.paymentProcess.entity.BankRules;
import com.example.paymentProcess.entity.Payments;
import com.example.paymentProcess.entity.StpConfigurations;
import lombok.Data;

import java.util.List;
@Data
public class PaymentFileByIDResponseDTO {

    private Payments paymentFile;
    private List<StpConfigurations> customerConfigurations;
    private List<BankRules> bankConfigurations;

}
