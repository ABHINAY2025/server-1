//package com.example.demo.controller.controlle;
//
//import com.example.demo.dto.PaymentFileByIDResponseDTO;
//import com.example.demo.entity.*;
//import com.example.demo.service.servic.PaymentFilesService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//public class PaymentFilesController {
//
//    private final PaymentFilesService paymentFilesService;
//
//    public PaymentFilesController(PaymentFilesService paymentFilesService) {
//        this.paymentFilesService = paymentFilesService;
//    }
//
//    // Endpoint to get all PaymentFiles
//    @GetMapping("/getAll/paymentFiles")
//    public List<PaymentFile> getAllPaymentsFiles() {
//        return paymentFilesService.getAllPaymentsFiles();
//    }
//
//    @GetMapping("/get/paymentFile/{id}")
//    public Optional<Payments> getPaymentFileById(@PathVariable String id){
//        return paymentFilesService.getPaymentFileBy(id);
//    }
//
//    // Endpoint to get a payment File by _id with Rules
//    @GetMapping("/paymentFile/{id}")
//    public ResponseEntity<PaymentFileByIDResponseDTO> getTransactionById(@PathVariable String id) {
//        PaymentFileByIDResponseDTO paymentFile = paymentFilesService.getPaymentFileById(id);
//        if (paymentFile == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(paymentFile);
//    }
//
//    // APi for the Transaction page
//    @GetMapping("/transactionPage")
//    public ResponseEntity<Map<String, Object>> getAllTransactions() {
//        List<PaymentFile> paymentFiles = paymentFilesService.getAllPaymentsFiles();
//
//        // Sum up the BigDecimal values of ctrlSum directly
//        BigDecimal totalAmount = paymentFiles.stream()
//                .map(PaymentFile::getCtrlSum)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        Long totalTransactions = (long) paymentFiles.size();
//
//        // Create a response map with the required fields
//        Map<String, Object> response = new HashMap<>();
//        response.put("paymentFiles", paymentFiles);
//        response.put("totalAmount", totalAmount);
//        response.put("totalTransactions", totalTransactions);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // Api for the bank rule by id
//    @GetMapping("/bankRule/{id}")
//    public ResponseEntity<BankRules> getBankRuleById(@PathVariable String id){
//        BankRules bankRules = paymentFilesService.getByBankRuleId(id);
//        if (bankRules == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(bankRules);
//    }
//
//    // Api for the bank rule by id
//    @GetMapping("/getBankRule/{name}")
//    public ResponseEntity<List<BankRules>> getBankRuleByName(@RequestParam(required = false) String name){
//
//        List<BankRules> bankRules;
//        if(name != null && !name.isEmpty()){
//            bankRules = paymentFilesService.getByBankRuleName(name);
//        }else {
//            bankRules = paymentFilesService.getAllBankRules();
//        }
//        return ResponseEntity.ok(bankRules);
//    }
//
//    // API for the get all Bank Rules
//    @GetMapping("/bankRules")
//    public ResponseEntity<List<BankRules>> getAllBankRules(){
//        return ResponseEntity.ok(paymentFilesService.getAllBankRules());
//    }
//
//    // Api for the bank rule by id
//    @GetMapping("/stpConfig/{id}")
//    public ResponseEntity<StpConfigurations> getStpConfigById(@PathVariable String id){
//        StpConfigurations stpConfigId = paymentFilesService.getByStpConfigId(id);
//        if (stpConfigId == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(stpConfigId);
//    }
//
//    // API for the get all stp configurations
//    @GetMapping("/stpConfigurations")
//    public ResponseEntity<List<StpConfigurations>> getAllStpConfigurations(){
//        return ResponseEntity.ok(paymentFilesService.getAllStpConfigurations());
//    }
//
//    @GetMapping("/stpConfigurations/{name}")
//    public ResponseEntity<List<StpConfigurations>> getAllStpConfigurationsByName(@PathVariable String name){
//        return ResponseEntity.ok(paymentFilesService.getStpConfigurationsByCustomerName(name));
//    }
//
//    // Api for the all Customer names
//    @GetMapping("getAll/customerNames")
//    public ResponseEntity<List<Customer>> getAllCustomerNames(){
//        return ResponseEntity.ok(paymentFilesService.getAllCustomerNames());
//    }
//
//    //Api for the ISO standard fields
//    @GetMapping("/isoStandardFields")
//    public ResponseEntity<List<IsoStandardFields>> getAllIsoFields(){
//        return ResponseEntity.ok(paymentFilesService.getAllStandardFields());
//    }
//
//    // API to create STP configuration
//    @PostMapping("/createStpConfig")
//    public ResponseEntity<StpConfigurations> createStpConfig(@RequestBody StpConfigurations stpConfigurations){
//        return ResponseEntity.ok(paymentFilesService.createStpConfiguration(stpConfigurations));
//    }
//
//    //API to create Bank Rules
//    @PostMapping("/createBankRule")
//    public ResponseEntity<BankRules> createBankRule(@RequestBody BankRules bankRules){
//        return ResponseEntity.ok(paymentFilesService.createBankRule(bankRules));
//    }
//
//    //Api to get Companies by ID
//    @GetMapping("/companies/{id}")
//    public ResponseEntity<Companies> getById(@PathVariable String id){
//        return ResponseEntity.ok(paymentFilesService.getCompaniesById(id));
//    }
//
//    //Api to get Companies by ID
//    @GetMapping("getAll/companies")
//    public ResponseEntity<List<Companies>> getAllCompanies(){
//        return ResponseEntity.ok(paymentFilesService.getAllCompanies());
//    }
//
//    // Api to get the duplicates from the Bank Rules
//    @GetMapping("/bankRules/duplicates")
//    public List<BankRules> getDuplicateBankRules() {
//        return paymentFilesService.getDuplicateBankRules();
//    }
//
//    // Api to get the duplicates from the Stp Configurations
//    @GetMapping("/stpConfigurations/duplicates")
//    public List<StpConfigurations> getDuplicateStpConfigurations() {
//        return paymentFilesService.getDuplicateStpConfigurations();
//    }
//
//    //Api to get All Networks from the Networks table
//    @GetMapping("/getAll/networks")
//    public ResponseEntity<List<Networks>> getAllNetworks(){
//        return ResponseEntity.ok(paymentFilesService.getAllNetworks());
//    }
//
//    //Api to get NetworkById from the Networks Table
//    @GetMapping("/get/network/{name}")
//    public ResponseEntity<Networks> getNetworkById(@PathVariable String name){
//        return ResponseEntity.ok(paymentFilesService.getNetworkByName(name));
//    }
//
//    //APi to get All AutoCorrected rules
//    @GetMapping("/getAll/autoCorrectedRules")
//    public ResponseEntity<List<AutoCorrectRules>> getAllAutoCorrectedRules(){
//        return ResponseEntity.ok(paymentFilesService.getAllAutoCorrectRules());
//    }
//
//    //Api to get AutoCorrect Rule By I'd
//    @GetMapping("/get/autoCorrectRule/{id}")
//    public ResponseEntity<AutoCorrectRules> getAutoCorrectRuleById(@PathVariable String id){
//        return ResponseEntity.ok(paymentFilesService.getAutoCorrectRuleById(id));
//    }
//
//    @PutMapping("/approve/{id}")
//    public String approvePayment(@PathVariable String id, @RequestBody String updatedXml) {
//        // Call service method to update payment and create AutoCorrectRules record
//        boolean isUpdated = paymentFilesService.approvePaymentFile(id, updatedXml);
//
//        if (isUpdated) {
//            return "Payment approved and AutoCorrectRules created successfully.";
//        } else {
//            return "Failed to update payment.";
//        }
//    }
//
//
//}
