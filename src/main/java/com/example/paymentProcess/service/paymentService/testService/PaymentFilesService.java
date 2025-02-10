//package com.example.demo.service.servic;
//
//import com.example.demo.dto.PaymentFileByIDResponseDTO;
//import com.example.demo.entity.*;
//import com.example.demo.repository.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//import java.io.StringReader;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class PaymentFilesService {
//
//    private final NetworksRepository networksRepository;
//    private final PaymentFileRepository paymentFileRepository;
//    private final BankRulesRepository bankRulesRepository;
//    private final StpConfigurationRepository stpConfigurationRepository;
//    private final CustomerRepository customerRepository;
//    private final IsoStandardFieldsRepository isoStandardFieldsRepository;
//    private final CompaniesRepository companiesRepository;
//    private final AutoCorrectedRulesRepository autoCorrectedRulesRepository;
//    private final PaymentsRepository paymentsRepository;
//
//
//    public PaymentFilesService(NetworksRepository networksRepository, PaymentFileRepository paymentFileRepository, BankRulesRepository bankRulesRepository, StpConfigurationRepository stpConfigurationRepository, CustomerRepository customerRepository, IsoStandardFieldsRepository isoStandardFieldsRepository, CompaniesRepository companiesRepository, AutoCorrectedRulesRepository autoCorrectedRulesRepository, PaymentsRepository paymentsRepository) {
//        this.networksRepository = networksRepository;
//        this.paymentFileRepository = paymentFileRepository;
//        this.bankRulesRepository = bankRulesRepository;
//        this.stpConfigurationRepository = stpConfigurationRepository;
//        this.customerRepository = customerRepository;
//        this.isoStandardFieldsRepository = isoStandardFieldsRepository;
//        this.companiesRepository = companiesRepository;
//        this.autoCorrectedRulesRepository = autoCorrectedRulesRepository;
//        this.paymentsRepository = paymentsRepository;
//    }
//
//    // Method to fetch all PaymentsFiles
//    public List<PaymentFile> getAllPaymentsFiles() {
//        return paymentFileRepository.findAll();
//    }
//
//
//    public Optional<Payments> getPaymentFileBy(String id) {
//        return paymentsRepository.findById(id);
//    }
//
//    //Method to fetch Payment File by id with BAnk And customer rule on particular id
//    public PaymentFileByIDResponseDTO getPaymentFileById(String id) {
//        PaymentFileByIDResponseDTO paymentFileByIDResponseDTO = new PaymentFileByIDResponseDTO();
//
//        Optional<PaymentFile> paymentFile = paymentFileRepository.findById(id);
//        if (paymentFile.isPresent()) {
//            PaymentFile paymentFile1 = paymentFile.get();
////            paymentFileByIDResponseDTO.setPaymentFile(paymentFile1);
//
//            List<String> allBankRuleIds = new ArrayList<>();
//            List<String> allCustomerRuleIds = new ArrayList<>();
//
//            for (PaymentFile.RuleID ruleSet : paymentFile1.getRuleIDs()) {
//                allBankRuleIds.addAll(ruleSet.getBank_rules());
//
//                allCustomerRuleIds.addAll(ruleSet.getCustomer_rules());
//            }
//
//            List<BankRules> bankRules = bankRulesRepository.findAllById(allBankRuleIds);
//            List<StpConfigurations> customerRules = stpConfigurationRepository.findAllById(allCustomerRuleIds);
//
//            paymentFileByIDResponseDTO.setBankConfigurations(bankRules);
//            paymentFileByIDResponseDTO.setCustomerConfigurations(customerRules);
//
//        } else {
//            throw new RuntimeException("Payment file not found with id: " + id);
//        }
//
//        return paymentFileByIDResponseDTO;
//    }
//
//    public List<Networks> getAllNetworks(){
//        return networksRepository.findAll();
//    }
//
//    public Networks getNetworkByName(String name){
//        return networksRepository.findByName(name);
//    }
//
//    // method to fetch bank rule by id
//    public BankRules getByBankRuleId(String id){
//        return bankRulesRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank Rule not found with id: " + id));
//    }
//
//    // Method to get bank rules by name
//    public List<BankRules> getByBankRuleName(String name) {
//        String decode = URLDecoder.decode(name, StandardCharsets.UTF_8);
//        return bankRulesRepository.findByBankName(decode);
//    }
//
//    // Method get al Bank Rules
//    public List<BankRules> getAllBankRules() {
//        return bankRulesRepository.findAll();
//    }
//
//    // method to fetch STP Configurations by id
//    public StpConfigurations getByStpConfigId(String id){
//        return stpConfigurationRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer Rule not found with id: " + id));
//    }
//
//    //Method to get all STP Configurations
//    public List<StpConfigurations> getAllStpConfigurations() {
//        return stpConfigurationRepository.findAll();
//    }
//
//    //Method to get all STP Configurations by name
//    public List<StpConfigurations> getStpConfigurationsByCustomerName(String name) {
//        String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);
//        return stpConfigurationRepository.findByCustomerName(decodedName);
//    }
//
//    // method to fetch all company names
//    public List<Customer> getAllCustomerNames(){
//        return customerRepository.findAll();
//    }
//
//    // method to fetch all ISO Standard Fields
//    public List<IsoStandardFields> getAllStandardFields(){
//        return isoStandardFieldsRepository.findAll();
//    }
//
//    // Method to create Bank Rule
//    public BankRules createBankRule(BankRules bankRules){
//        return bankRulesRepository.save(bankRules);
//    }
//
//    // Method to create STP Configuration
//    public StpConfigurations createStpConfiguration(StpConfigurations stpConfigurations){
//        return stpConfigurationRepository.save(stpConfigurations);
//    }
//
//    // Method to get Companies names by ID
//    public Companies getCompaniesById(String id){
//        return companiesRepository.findById(id).orElseThrow(() -> new RuntimeException("Companies not Found with id : " + id));
//    }
//
//    public List<Companies> getAllCompanies() {
//        return companiesRepository.findAll();
//    }
//
//    //Method to get duplicates form bankRules
//    public List<BankRules> getDuplicateBankRules() {
//        List<BankRules> allRecords = bankRulesRepository.findAll();
//
//        // Group by ruleName and networkName and find duplicates
//        Map<String, List<BankRules>> grouped = allRecords.stream()
//                .collect(Collectors.groupingBy(rule -> rule.getRuleName() + ":" + rule.getNetworkName()));
//
//        // Filter groups that have more than one record (duplicates)
//        return grouped.values().stream()
//                .filter(rules -> rules.size() > 1)
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
//    }
//
//    // Method to get duplicates from STP Configurations
//    public List<StpConfigurations> getDuplicateStpConfigurations() {
//        List<StpConfigurations> allRecords = stpConfigurationRepository.findAll();
//
//        // Group by ruleName and networkName (you can change the criteria as needed)
//        Map<String, List<StpConfigurations>> grouped = allRecords.stream()
//                .collect(Collectors.groupingBy(config -> config.getRuleName() + ":" + config.getNetworkName()));
//
//        // Filter groups that have more than one record (duplicates)
//        return grouped.values().stream()
//                .filter(configs -> configs.size() > 1)
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
//    }
//
//    //Method to fetch all details form the AutoCorrectRules
//    public List<AutoCorrectRules> getAllAutoCorrectRules(){
//        List<AutoCorrectRules> autoCorrectRules = autoCorrectedRulesRepository.findAll();
//        log.info("autoCorrectedRules : {}",autoCorrectRules);
//        return autoCorrectRules;
//    }
//
//    //Method to fetch  details by I'd from the autocorrect rules
//    public AutoCorrectRules getAutoCorrectRuleById(String id){
//        return autoCorrectedRulesRepository.findById(id).orElseThrow(() -> new RuntimeException("autocorrect rule not found with id : "+ id));
//    }
//
////    public boolean approvePaymentFile(String id, String updatedXml) {
////        // Fetch the PaymentFile object by id
////        PaymentFile paymentFile = paymentFileRepository.findById(id).orElse(null);
////
////        if (paymentFile != null) {
////            // Update the PaymentFile with the updatedXml and set the file status to "Approval"
////            paymentFile.setUpdatedXml(updatedXml);
////            paymentFile.setFileStatus("Approved");
////
////            // Save the updated PaymentFile
////            paymentFileRepository.save(paymentFile);
////
////            // Create a new AutoCorrectRules record
////            AutoCorrectRules autoCorrectRules = createAutoCorrectRules(paymentFile, updatedXml);
////
////            // Save the AutoCorrectRules object in the database
////            autoCorrectedRulesRepository.save(autoCorrectRules);
////
////            return true;
////        } else {
////            return false;
////        }
////    }
////
////    private AutoCorrectRules createAutoCorrectRules(PaymentFile paymentFile, String updatedXml) {
////        AutoCorrectRules autoCorrectRules = new AutoCorrectRules();
////        autoCorrectRules.setMessageInfoId(paymentFile.getMsgId());
////
////        // Extract debtor or creditor details based on the PaymentFile
////        if (paymentFile.getDebtorData() != null) {
////            // If the type is debtor
////            autoCorrectRules.setType("Dbtr");
////            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Dbtr"));
////            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(updatedXml, "Dbtr"));
////        } else if (paymentFile.getCreditorData() != null) {
////            // If the type is creditor
////            autoCorrectRules.setType("Cdtr");
////            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Cdtr"));
////            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(updatedXml, "Cdtr"));
////        }
////
////        return autoCorrectRules;
////    }
////
////    // Method to extract AcctDetails from XML based on debtor or creditor type
////    private AutoCorrectRules.AcctDetails extractAcctDetails(String xml, String type) {
////        AutoCorrectRules.AcctDetails acctDetails = new AutoCorrectRules.AcctDetails();
////
////        // Logic to parse the XML and extract the necessary fields based on debtor or creditor type
////        if (type.equals("Dbtr")) {
////            // Extract debtor details (replace with actual XML parsing logic)
////            acctDetails.setIBAN("Debtor IBAN");
////            acctDetails.setName("Debtor Name");
////            acctDetails.setBIC("Debtor BIC");
////            acctDetails.setBankName("Debtor Bank");
////            acctDetails.setCategory("BIC");
////        } else if (type.equals("Cdtr")) {
////            // Extract creditor details (replace with actual XML parsing logic)
////            acctDetails.setIBAN("Creditor IBAN");
////            acctDetails.setName("Creditor Name");
////            acctDetails.setBIC("Creditor BIC");
////            acctDetails.setBankName("Creditor Bank");
////            acctDetails.setCategory("BIC");
////        }
////
////        return acctDetails;
////    }
//
//    public boolean approvePaymentFile(String id, String updatedXml) {
//        // Fetch the PaymentFile object by id
//        PaymentFile paymentFile = paymentFileRepository.findById(id).orElse(null);
//
//        if (paymentFile != null) {
//            // Update the PaymentFile with the updatedXml and set the file status to "Approved"
//            paymentFile.setUpdatedXml(updatedXml);
//            paymentFile.setFileStatus("Approved");
//
//            // Save the updated PaymentFile
//            paymentFileRepository.save(paymentFile);
//
//            // Create a new AutoCorrectRules record
//            AutoCorrectRules autoCorrectRules = createAutoCorrectRules(paymentFile, updatedXml);
//
//            // Save the AutoCorrectRules object in the database
//            autoCorrectedRulesRepository.save(autoCorrectRules);
//
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private AutoCorrectRules createAutoCorrectRules(PaymentFile paymentFile, String updatedXml) {
//        AutoCorrectRules autoCorrectRules = new AutoCorrectRules();
//
//        // Set messageInfoId as the msgId of the PaymentFile
//        autoCorrectRules.setMessageInfoId(paymentFile.getMsgId()); // msgId -> messageInfoId mapping
//
//        // Extract debtor or creditor details based on the PaymentFile
//        if (paymentFile.getDebtorData() != null) {
//            // If the type is debtor
//            autoCorrectRules.setType("Dbtr");
//            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Dbtr"));
//            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(updatedXml, "Dbtr"));
//        } else if (paymentFile.getCreditorData() != null) {
//            // If the type is creditor
//            autoCorrectRules.setType("Cdtr");
//            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Cdtr"));
//            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(updatedXml, "Cdtr"));
//        }
//
//        return autoCorrectRules;
//    }
//
//    // Method to extract AcctDetails from XML based on debtor or creditor type
//    private AutoCorrectRules.AcctDetails extractAcctDetails(String xml, String type) {
//        AutoCorrectRules.AcctDetails acctDetails = new AutoCorrectRules.AcctDetails();
//
//        try {
//            // Parse the XML string to a Document object
//            Document document = parseXmlStringToDocument(xml);
//
//            // Logic to extract fields based on debtor or creditor type
//            if (type.equals("Dbtr")) {
//                // Extract debtor details
//                Payments.Debtor debtor = extractDebtorDetails(document);
//                acctDetails.setIBAN(debtor.getDebtorIban());
//                acctDetails.setName(debtor.getDebtorName());
//                acctDetails.setBIC(debtor.getDebtorBic());
//                acctDetails.setBankName(debtor.getDebtorAddress()); // Assuming debtor address or bank name
//                acctDetails.setCategory("BIC");
//            } else if (type.equals("Cdtr")) {
//                // Extract creditor details
//                Payments.Creditor creditor = extractCreditorDetails(document);
//                acctDetails.setIBAN(creditor.getCreditorIban());
//                acctDetails.setName(creditor.getCreditorName());
//                acctDetails.setBIC(creditor.getCreditorBic());
//                acctDetails.setBankName(creditor.getCreditorAddress()); // Assuming creditor address or bank name
//                acctDetails.setCategory("BIC");
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // Log the error to understand failure in parsing or extraction
//        }
//
//        return acctDetails;
//    }
//
//    // Method to parse the XML string to a Document object
//    private Document parseXmlStringToDocument(String xml) throws Exception {
//        // Remove BOM if it exists (if XML encoding contains BOM)
//        if (xml.startsWith("\uFEFF")) {
//            xml = xml.substring(1);
//        }
//
//        // Trim any leading or trailing whitespace
//        xml = xml.trim();
//
//        // Check if the XML starts with the expected format (either the XML declaration or a root element)
//        if (!xml.startsWith("<?xml") && !xml.startsWith("<")) {
//            throw new IllegalArgumentException("Invalid XML content: " + xml);
//        }
//
//        // Parse the XML
//        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
//        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
//        InputSource is = new InputSource(new StringReader(xml));
//        return builder.parse(is);
//    }
//
//    // Extract debtor details from the document
//    private Payments.Debtor extractDebtorDetails(Document document) {
//        Payments.Debtor debtor = new Payments.Debtor();
//
//        // Extract debtor name
//        NodeList debtorNameNodes = document.getElementsByTagName("ns0:Dbtr");
//        if (debtorNameNodes.getLength() > 0) {
//            NodeList debtorNameNodeList = ((org.w3c.dom.Element) debtorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
//            if (debtorNameNodeList.getLength() > 0) {
//                debtor.setDebtorName(cleanUpString(debtorNameNodeList.item(0).getTextContent()));
//            }
//        }
//
//        // Extract debtor address
//        NodeList debtorAddressNodes = document.getElementsByTagName("ns0:Dbtr");
//        if (debtorAddressNodes.getLength() > 0) {
//            Node debtorNode = debtorAddressNodes.item(0);
//            StringBuilder debtorAddress = new StringBuilder();
//
//            // Extract Street Name
//            NodeList debtorStreetNameNodes = ((org.w3c.dom.Element) debtorNode).getElementsByTagName("ns0:StrtNm");
//            for (int i = 0; i < debtorStreetNameNodes.getLength(); i++) {
//                Node streetNode = debtorStreetNameNodes.item(i);
//                if (streetNode != null) {
//                    debtorAddress.append(cleanUpString(streetNode.getTextContent())).append(" ");
//                }
//            }
//
//            // Extract Town Name (or any other address component if needed)
//            NodeList debtorTownNameNodes = ((org.w3c.dom.Element) debtorNode).getElementsByTagName("ns0:TwnNm");
//            for (int i = 0; i < debtorTownNameNodes.getLength(); i++) {
//                Node townNode = debtorTownNameNodes.item(i);
//                if (townNode != null) {
//                    debtorAddress.append(cleanUpString(townNode.getTextContent())).append(" ");
//                }
//            }
//
//            // Set the complete address
//            debtor.setDebtorAddress(debtorAddress.toString().trim());
//        }
//
//        // Extract debtor BIC and IBAN
//        debtor.setDebtorBic(extractDebtorBic(document));
//        debtor.setDebtorIban(extractDebtorIban(document));
//
//        return debtor;
//    }
//
//    // Extract BIC from debtor
//    private String extractDebtorBic(Document document) {
//        NodeList debtorBicNodes = document.getElementsByTagName("ns0:DbtrAgt");
//        if (debtorBicNodes.getLength() > 0) {
//            Node debtorBicNode = ((org.w3c.dom.Element) debtorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
//            if (debtorBicNode != null) {
//                return cleanUpString(debtorBicNode.getTextContent());
//            }
//        }
//        return "";
//    }
//
//    // Extract IBAN from debtor
//    private String extractDebtorIban(Document document) {
//        NodeList debtorIbanNodes = document.getElementsByTagName("ns0:DbtrAcct");
//        if (debtorIbanNodes.getLength() > 0) {
//            Node debtorIbanNode = ((org.w3c.dom.Element) debtorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
//            if (debtorIbanNode != null) {
//                return cleanUpString(debtorIbanNode.getTextContent());
//            }
//        }
//        return "";
//    }
//
//    // Extract creditor details from the document
//    private Payments.Creditor extractCreditorDetails(Document document) {
//        Payments.Creditor creditor = new Payments.Creditor();
//
//        // Extract creditor name
//        NodeList creditorNameNodes = document.getElementsByTagName("ns0:Cdtr");
//        if (creditorNameNodes.getLength() > 0) {
//            NodeList creditorNameNodeList = ((org.w3c.dom.Element) creditorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
//            if (creditorNameNodeList.getLength() > 0) {
//                creditor.setCreditorName(cleanUpString(creditorNameNodeList.item(0).getTextContent()));
//            }
//        }
//
//        // Extract creditor address
//        NodeList creditorAddressNodes = document.getElementsByTagName("ns0:Cdtr");
//        if (creditorAddressNodes.getLength() > 0) {
//            Node creditorNode = creditorAddressNodes.item(0);
//            StringBuilder creditorAddress = new StringBuilder();
//
//            // Extract Street Name
//            NodeList streetNameNodes = ((org.w3c.dom.Element) creditorNode).getElementsByTagName("ns0:StrtNm");
//            for (int i = 0; i < streetNameNodes.getLength(); i++) {
//                Node streetNode = streetNameNodes.item(i);
//                if (streetNode != null) {
//                    creditorAddress.append(cleanUpString(streetNode.getTextContent())).append(" ");
//                }
//            }
//
//            // Extract Town Name (or any other address component if needed)
//            NodeList townNameNodes = ((org.w3c.dom.Element) creditorNode).getElementsByTagName("ns0:TwnNm");
//            for (int i = 0; i < townNameNodes.getLength(); i++) {
//                Node townNode = townNameNodes.item(i);
//                if (townNode != null) {
//                    creditorAddress.append(cleanUpString(townNode.getTextContent())).append(" ");
//                }
//            }
//
//            // Set the complete address
//            creditor.setCreditorAddress(creditorAddress.toString().trim());
//        }
//
//        // Extract creditor BIC and IBAN
//        creditor.setCreditorBic(extractCreditorBic(document));
//        creditor.setCreditorIban(extractCreditorIban(document));
//
//        return creditor;
//    }
//
//    // Extract BIC from creditor
//    private String extractCreditorBic(Document document) {
//        NodeList creditorBicNodes = document.getElementsByTagName("ns0:CdtrAgt");
//        if (creditorBicNodes.getLength() > 0) {
//            Node creditorBicNode = ((org.w3c.dom.Element) creditorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
//            if (creditorBicNode != null) {
//                return cleanUpString(creditorBicNode.getTextContent());
//            }
//        }
//        return "";
//    }
//
//    // Extract IBAN from creditor
//    private String extractCreditorIban(Document document) {
//        NodeList creditorIbanNodes = document.getElementsByTagName("ns0:CdtrAcct");
//        if (creditorIbanNodes.getLength() > 0) {
//            Node creditorIbanNode = ((org.w3c.dom.Element) creditorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
//            if (creditorIbanNode != null) {
//                return cleanUpString(creditorIbanNode.getTextContent());
//            }
//        }
//        return "";
//    }
//
//    private String cleanUpString(String input) {
//        // Clean up any unnecessary whitespace or characters in the string
//        return input != null ? input.trim() : "";
//    }
//
//
//
//}
