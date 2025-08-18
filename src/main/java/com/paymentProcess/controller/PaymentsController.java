package com.paymentProcess.controller;

import com.paymentProcess.entity.BankRules;
import com.paymentProcess.entity.Companies;
import com.paymentProcess.entity.Payments;
import com.paymentProcess.entity.StpConfigurations;
import com.paymentProcess.service.paymentService.PaymentsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class PaymentsController {

    private final PaymentsServiceImpl paymentsService;

    public PaymentsController(PaymentsServiceImpl paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/processPayment")
    public Payments processPayment(@RequestBody String originalXml) throws Exception {
        return paymentsService.processPayment(originalXml).getBody();
    }

    @GetMapping("/get/paymentFile/{id}")
    public ResponseEntity<?> getPaymentFileById(@PathVariable String id) {
        Optional<Payments> paymentFile = paymentsService.getPaymentFileBy(id);
        if (paymentFile.isPresent()) {
            return ResponseEntity.ok(paymentFile.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment file not found with id : " + id);
        }
    }

    // Endpoint to get all PaymentFiles
    @GetMapping("/getAll/paymentFiles")
    public ResponseEntity<?> getAllPaymentsFiles() {
        try {
            List<Payments> paymentFiles = paymentsService.getAllPaymentsFiles();

            if (paymentFiles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No payment files found");
            }

            return ResponseEntity.ok(paymentFiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching payment files: " + e.getMessage());
        }
    }

    // Endpoint to get a payment File by _id with Rules
    @GetMapping("/paymentFile/{id}")
    public ResponseEntity<?> getPaymentFileByIdWithRules(@PathVariable String id) {
        try {
            return ResponseEntity.ok(paymentsService.getTransactionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

        // APi for the Transaction page
        @GetMapping("/transactionPage")
        public ResponseEntity<Map<String, Object>> getAllTransactions(
                @RequestParam(required = false) String fileStatus) {

//        System.out.print(fileStatus);


            List<Payments> paymentFiles;
            if (fileStatus != null && !fileStatus.isEmpty()) {
                paymentFiles = paymentsService.getPaymentsByStatus(fileStatus);
            } else {
                paymentFiles = paymentsService.getAllPaymentsFiles();
            }
            Double totalAmount = paymentFiles.stream()
                    .map(Payments::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(0.0, Double::sum);

            Long totalTransactions = (long) paymentFiles.size();

            Map<String, Object> response = new HashMap<>();
            response.put("paymentFiles", paymentFiles);
            response.put("totalAmount", totalAmount);
            response.put("totalTransactions", totalTransactions);

            return ResponseEntity.ok(response);
        }


    //Api to get All Networks from the Networks table
    @GetMapping("/getAll/networks")
    public ResponseEntity<?> getAllNetworks() {
        try {
            return ResponseEntity.ok(paymentsService.getAllNetworks());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/network/{name}")
    public ResponseEntity<?> getNetworkByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(paymentsService.getNetworkByName(name));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/bankRule/{id}")
    public ResponseEntity<?> getByBankRuleId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(paymentsService.getByBankRuleId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/bankRules/{name}")
        public ResponseEntity<List<BankRules>> getBankRuleByName(@RequestParam(required = false) String name){

        List<BankRules> bankRules;
        if(name != null && !name.isEmpty()){
            bankRules = paymentsService.getByBankRuleName(name);
        }else {
            bankRules = paymentsService.getAllBankRules();
        }
        return ResponseEntity.ok(bankRules);
    }
    @GetMapping("/bankRules/batch")
    public ResponseEntity<?> getAllBankRules() {
        try {
            return ResponseEntity.ok(paymentsService.getAllBankRules());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @GetMapping("/stpConfiguration/{id}")
    public ResponseEntity<?> getByStpConfigId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(paymentsService.getByStpConfigId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @GetMapping("/customerRules/batch")
    public ResponseEntity<?> getAllStpConfigurations() {
        try {
            return ResponseEntity.ok(paymentsService.getAllStpConfigurations());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/stpConfigurations/{customerName}")
    public ResponseEntity<List<StpConfigurations>> getStpRuleByName(@PathVariable(required = false) String customerName){

        List<StpConfigurations> stpConfigurations;
        if(customerName != null && !customerName.isEmpty()){
            stpConfigurations = paymentsService.getStpConfigurationsByCustomerName(customerName);
        }else {
            stpConfigurations = paymentsService.getAllStpConfigurations();
        }
        return ResponseEntity.ok(stpConfigurations);
    }

    // Api for the all Customer names
    @GetMapping("/getAll/customerNames")
    public ResponseEntity<?> getAllCustomerNames() {
        try {
            return ResponseEntity.ok(paymentsService.getAllCustomerNames());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    //Api for the ISO standard fields
    @GetMapping("/isoStandardFields")
    public ResponseEntity<?> getAllStandardFields() {
        try {
            return ResponseEntity.ok(paymentsService.getAllStandardFields());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Endpoint to create a new bank rule
    @PostMapping("/create/bankRules")
    public ResponseEntity<BankRules> createBankRule(@RequestBody BankRules bankRules) {
        try {
            BankRules createdRule = paymentsService.createBankRule(bankRules);
            return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to create a new STP configuration
    @PostMapping("/create/stpConfigurations")
    public ResponseEntity<StpConfigurations> createStpConfiguration(@RequestBody StpConfigurations stpConfigurations) {
        try {
            StpConfigurations createdConfig = paymentsService.createStpConfiguration(stpConfigurations);
            return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get company by ID
    @GetMapping("/companies/{id}")
    public ResponseEntity<Companies> getCompanyById(@PathVariable("id") String id) {
        try {
            Companies company = paymentsService.getCompaniesById(id);
            return new ResponseEntity<>(company, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get all companies
    @GetMapping("/getAll/companies")
    public ResponseEntity<List<Companies>> getAllCompanies() {
        try {
            List<Companies> companies = paymentsService.getAllCompanies();
            return new ResponseEntity<>(companies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    // Endpoint to get duplicate bank rules
//    @GetMapping("/bankRules/duplicates/{name}")
//    public ResponseEntity<List<BankRules>> getDuplicateBankRules(@RequestParam(required = false) String name) {
//        try {
//            List<BankRules> bankRules;
//            if (name != null && !name.isEmpty()) {
//                bankRules = paymentsService.getByBankRuleName(name);
//            } else {
//                bankRules = paymentsService.getAllBankRules();
//            }
//            return ResponseEntity.ok(bankRules);
//        }catch (Exception e){
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/bankRules/duplicates/{bankName}")
    public ResponseEntity<List<BankRules>> getDuplicateBankRules(@PathVariable(required = false) String bankName) {
        List<BankRules> duplicates = paymentsService.getDuplicateBankRules(bankName);
        return ResponseEntity.ok(duplicates);
    }


    @GetMapping("/stpConfigurations/duplicates/{customerName}")
    public ResponseEntity<List<StpConfigurations>> getDuplicateStpConfigurations(@PathVariable(required = false) String customerName) {
        List<StpConfigurations> duplicates = paymentsService.getDuplicateStpConfigurations(customerName);
        return ResponseEntity.ok(duplicates);
    }


//    // Endpoint to get duplicate STP configurations
//    @GetMapping("/stpConfigurations/duplicates/{name}")
//    public ResponseEntity<List<StpConfigurations>> getDuplicateStpConfigurations(@RequestParam(required = false) String name) {
//        try {
//            List<StpConfigurations> stpConfigurations;
//            if (name != null && !name.isEmpty()) {
//                stpConfigurations = paymentsService.getStpConfigurationsByCustomerName(name);
//            } else {
//                stpConfigurations = paymentsService.getAllStpConfigurations();
//            }
//            return ResponseEntity.ok(stpConfigurations);
//        }catch (Exception e){
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    //APi to get All AutoCorrected rules
    @GetMapping("/getAll/autoCorrectedRules")
    public ResponseEntity<?> getAllAutoCorrectRules() {
        try {
            return ResponseEntity.ok(paymentsService.getAllAutoCorrectRules());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    //Get AutoCorrectRule by ID
    @GetMapping("/get/autoCorrectRule/{id}")
    public ResponseEntity<?> getAutoCorrectRuleById(@PathVariable String id){
        try{
            return ResponseEntity.ok(paymentsService.getAutoCorrectRuleById(id));
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


}
