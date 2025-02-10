package com.example.paymentProcess.service.paymentService;

import com.example.paymentProcess.dto.PaymentFileByIDResponseDTO;
import com.example.paymentProcess.entity.*;
import com.example.paymentProcess.repository.*;
import com.example.paymentProcess.utility.PaymentProcessUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentsServiceImpl implements PaymentsService{

    private final PaymentsRepository paymentsRepository;
    private final PaymentProcessUtility paymentProcessUtility;
    private final BankRulesRepository bankRulesRepository;
    private final StpConfigurationRepository stpConfigurationRepository;
    private final NetworksRepository networksRepository;
    private final AutoCorrectedRulesRepository autoCorrectedRulesRepository;
    private final CustomerRepository customerRepository;
    private final IsoStandardFieldsRepository isoStandardFieldsRepository;
    private final CompaniesRepository companiesRepository;

    public PaymentsServiceImpl(PaymentsRepository paymentsRepository, PaymentProcessUtility paymentProcessUtility, BankRulesRepository bankRulesRepository, StpConfigurationRepository stpConfigurationRepository, NetworksRepository networksRepository, AutoCorrectedRulesRepository autoCorrectedRulesRepository, CustomerRepository customerRepository, IsoStandardFieldsRepository isoStandardFieldsRepository, CompaniesRepository companiesRepository) {
        this.paymentsRepository = paymentsRepository;
        this.paymentProcessUtility = paymentProcessUtility;
        this.bankRulesRepository = bankRulesRepository;
        this.stpConfigurationRepository = stpConfigurationRepository;
        this.networksRepository = networksRepository;
        this.autoCorrectedRulesRepository = autoCorrectedRulesRepository;
        this.customerRepository = customerRepository;
        this.isoStandardFieldsRepository = isoStandardFieldsRepository;
        this.companiesRepository = companiesRepository;
    }

    @Override
    public ResponseEntity<Payments> processPayment(String payload) throws Exception {
       Payments payments = paymentProcessUtility.ExtractPayment(payload);
       paymentsRepository.save(payments);
       return ResponseEntity.ok(payments);
    }

    //Method to fetch PaymentFile by ID
    @Override
    public Optional<Payments> getPaymentFileBy(String id) {
        try {
            return paymentsRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching payment files: " + e.getMessage(), e);
        }
    }

    // Method to fetch all PaymentsFiles
    @Override
    public List<Payments> getAllPaymentsFiles() {
        try{
            return paymentsRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error occurred while fetching payment files: " + e.getMessage(), e);
        }
    }

    // Method to fetch Payment File by ID with Bank and Customer rule
    @Override
    public PaymentFileByIDResponseDTO getTransactionById(String id) {
        PaymentFileByIDResponseDTO paymentFileByIDResponseDTO = new PaymentFileByIDResponseDTO();
        try {
            Optional<Payments> paymentFile = paymentsRepository.findById(id);
            if (paymentFile.isPresent()) {
                Payments paymentFile1 = paymentFile.get();
                paymentFileByIDResponseDTO.setPaymentFile(paymentFile1);

                List<String> allBankRuleIds = new ArrayList<>();
                List<String> allCustomerRuleIds = new ArrayList<>();

                for (Payments.RuleID ruleSet : paymentFile1.getRuleIDs()) {
                    allBankRuleIds.addAll(ruleSet.getBank_rules());
                    allCustomerRuleIds.addAll(ruleSet.getCustomer_rules());
                }

                List<BankRules> bankRules = bankRulesRepository.findAllById(allBankRuleIds);
                List<StpConfigurations> customerRules = stpConfigurationRepository.findAllById(allCustomerRuleIds);

                paymentFileByIDResponseDTO.setBankConfigurations(bankRules);
                paymentFileByIDResponseDTO.setCustomerConfigurations(customerRules);
            } else {
                throw new RuntimeException("Payment file not found with id: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching payment file by ID: " + e.getMessage(), e);
        }
        return paymentFileByIDResponseDTO;
    }

    // Fetch all Networks
    @Override
    public List<Networks> getAllNetworks() {
        try {
            return networksRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching networks: " + e.getMessage(), e);
        }
    }

    // Get Network by name
    @Override
    public Networks getNetworkByName(String name) {
        try {
            return networksRepository.findByName(name);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching network by name: " + e.getMessage(), e);
        }
    }

    // Fetch bank rule by ID
    @Override
    public BankRules getByBankRuleId(String id) {
        try {
            return bankRulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bank Rule not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching bank rule by ID: " + e.getMessage(), e);
        }
    }

    // Fetch bank rules by name
    @Override
    public List<BankRules> getByBankRuleName(String name) {
        try {
            String decode = URLDecoder.decode(name, StandardCharsets.UTF_8);
            return bankRulesRepository.findByBankName(decode);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching bank rules by name: " + e.getMessage(), e);
        }
    }

    // Get all Bank Rules
    @Override
    public List<BankRules> getAllBankRules() {
        try {
            return bankRulesRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all bank rules: " + e.getMessage(), e);
        }
    }

    // Fetch STP Configuration by ID
    @Override
    public StpConfigurations getByStpConfigId(String id) {
        try {
            return stpConfigurationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer Rule not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching STP configuration by ID: " + e.getMessage(), e);
        }
    }

    // Get all STP Configurations
    @Override
    public List<StpConfigurations> getAllStpConfigurations() {
        try {
            return stpConfigurationRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all STP configurations: " + e.getMessage(), e);
        }
    }

    // Get STP Configurations by Customer Name
    @Override
    public List<StpConfigurations> getStpConfigurationsByCustomerName(String name) {
        try {
            String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);
            return stpConfigurationRepository.findByCustomerName(decodedName);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching STP configurations by customer name: " + e.getMessage(), e);
        }
    }

    // Fetch all Company names
    @Override
    public List<Customer> getAllCustomerNames() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching customer names: " + e.getMessage(), e);
        }
    }

    // Fetch all ISO Standard Fields
    @Override
    public List<IsoStandardFields> getAllStandardFields() {
        try {
            return isoStandardFieldsRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching ISO standard fields: " + e.getMessage(), e);
        }
    }

    // Create Bank Rule
    @Override
    public BankRules createBankRule(BankRules bankRules) {
        try {
            return bankRulesRepository.save(bankRules);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating bank rule: " + e.getMessage(), e);
        }
    }

    // Create STP Configuration
    @Override
    public StpConfigurations createStpConfiguration(StpConfigurations stpConfigurations) {
        try {
            return stpConfigurationRepository.save(stpConfigurations);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating STP configuration: " + e.getMessage(), e);
        }
    }

    // Get Companies by ID
    @Override
    public Companies getCompaniesById(String id) {
        try {
            return companiesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching company by ID: " + e.getMessage(), e);
        }
    }

    // Get all Companies
    @Override
    public List<Companies> getAllCompanies() {
        try {
            return companiesRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all companies: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BankRules> getDuplicateBankRules(String bankName) {
        try {
            List<BankRules> allRecords = bankRulesRepository.findAll();
            Map<String, List<BankRules>> grouped = allRecords.stream()
                    .collect(Collectors.groupingBy(rule -> rule.getRuleName() + ":" + rule.getNetworkName()));

            // If name is provided, filter the duplicates by name
            if (bankName != null && !bankName.isEmpty()) {
                return grouped.values().stream()
                        .filter(rules -> rules.size() > 1)
                        .flatMap(List::stream)
                        .filter(rule -> rule.getRuleName().equals(bankName))
                        .collect(Collectors.toList());
            }

            // If no name provided, return all duplicates
            return grouped.values().stream()
                    .filter(rules -> rules.size() > 1)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching duplicate bank rules: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StpConfigurations> getDuplicateStpConfigurations(String customerName) {
        try {
            List<StpConfigurations> allRecords = stpConfigurationRepository.findAll();
            Map<String, List<StpConfigurations>> grouped = allRecords.stream()
                    .collect(Collectors.groupingBy(config -> config.getRuleName() + ":" + config.getNetworkName()));

            // If customerName is provided, filter the duplicates by customerName
            if (customerName != null && !customerName.isEmpty()) {
                return grouped.values().stream()
                        .filter(configs -> configs.size() > 1)
                        .flatMap(List::stream)
                        .filter(config -> config.getCustomerName().equals(customerName))
                        .collect(Collectors.toList());
            }

            // If no customerName provided, return all duplicates
            return grouped.values().stream()
                    .filter(configs -> configs.size() > 1)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching duplicate STP configurations: " + e.getMessage(), e);
        }
    }

    // Get all AutoCorrectRules
    @Override
    public List<AutoCorrectRules> getAllAutoCorrectRules() {
        try {
            return autoCorrectedRulesRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all autocorrect rules: " + e.getMessage(), e);
        }
    }

    // Get AutoCorrectRule by ID
    @Override
    public AutoCorrectRules getAutoCorrectRuleById(String id) {
        try {
            return autoCorrectedRulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Auto-correct rule not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching autocorrect rule by ID: " + e.getMessage(), e);
        }
    }
}
