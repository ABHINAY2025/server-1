package com.example.paymentProcess.service.paymentService;

import com.example.paymentProcess.dto.PaymentFileByIDResponseDTO;
import com.example.paymentProcess.entity.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentsService {

    ResponseEntity<Payments> processPayment(String payload) throws Exception;

    Optional<Payments> getPaymentFileBy(String id);

    List<Payments> getAllPaymentsFiles();

    PaymentFileByIDResponseDTO getTransactionById(String id);

    List<Networks> getAllNetworks();

    Networks getNetworkByName(String name);

    BankRules getByBankRuleId(String id);

    List<BankRules> getByBankRuleName(String name);

    List<BankRules> getAllBankRules();

    StpConfigurations getByStpConfigId(String id);

    List<StpConfigurations> getAllStpConfigurations();

    List<StpConfigurations> getStpConfigurationsByCustomerName(String name);

    List<Customer> getAllCustomerNames();

    List<IsoStandardFields> getAllStandardFields();

    BankRules createBankRule(BankRules bankRules);

    StpConfigurations createStpConfiguration(StpConfigurations stpConfigurations);

    Companies getCompaniesById(String id);

    List<Companies> getAllCompanies();

    List<BankRules> getDuplicateBankRules(String bankName);

    List<StpConfigurations> getDuplicateStpConfigurations(String customerName);

    List<AutoCorrectRules> getAllAutoCorrectRules();

    AutoCorrectRules getAutoCorrectRuleById(String id);
}
