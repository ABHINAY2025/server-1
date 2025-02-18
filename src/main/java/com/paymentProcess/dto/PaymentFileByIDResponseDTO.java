package com.paymentProcess.dto;

import com.paymentProcess.entity.BankRules;
import com.paymentProcess.entity.Payments;
import com.paymentProcess.entity.StpConfigurations;
import lombok.Data;

import java.util.List;
@Data
public class PaymentFileByIDResponseDTO {

    private Payments paymentFile;
    private List<StpConfigurations> customerConfigurations;
    private List<BankRules> bankConfigurations;

}
