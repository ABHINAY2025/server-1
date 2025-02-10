package com.example.paymentProcess.utility.paymentFileUtility;

import com.example.paymentProcess.entity.AutoCorrectRules;
import com.example.paymentProcess.entity.BankRules;
import com.example.paymentProcess.entity.Payments;
import com.example.paymentProcess.repository.AutoCorrectedRulesRepository;
import com.example.paymentProcess.repository.BankRulesRepository;
import com.example.paymentProcess.repository.BicRepository;
import com.example.paymentProcess.repository.IbanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
@Component
public class AutoCorrectUtility {

    private final AutoCorrectedRulesRepository autoCorrectedRulesRepository;
    private final BicRepository bicRepository;
    private final IbanRepository ibanRepository;
    private final BankRulesRepository bankRulesRepository;

    public AutoCorrectUtility(AutoCorrectedRulesRepository autoCorrectedRulesRepository, BicRepository bicRepository, IbanRepository ibanRepository, BankRulesRepository bankRulesRepository) {
        this.autoCorrectedRulesRepository = autoCorrectedRulesRepository;
        this.bicRepository = bicRepository;
        this.ibanRepository = ibanRepository;
        this.bankRulesRepository = bankRulesRepository;
    }

    public Payments.AutoCorrected compareAndCorrect(Payments payment) {
        boolean debtorAutoCorrected = false;
        boolean creditorAutoCorrected = false;

        // Get debtor and creditor details from the payment
        Payments.Debtor debtor = payment.getDebtor();
        Payments.Creditor creditor = payment.getCreditor();

        Payments.MlSuggestion cdtrMlSuggestion = payment.getCdtrMlSuggestion();
        Payments.MlSuggestion dbtrMlSuggestion = payment.getDbtrMlSuggestion();

        boolean debtorBicIssue = false;
        boolean creditorBicIssue = false;

        if (payment.getBicIssues() != null && !payment.getBicIssues().isEmpty()) {
            for (Payments.BicIssue bicIssue : payment.getBicIssues()) {
                // Check if any of the BicIssue entries have debtor or creditor BIC issues
                if (bicIssue.isDebtorBicIssue()) {
                    debtorBicIssue = true;
                }
                if (bicIssue.isCreditorBicIssue()) {
                    creditorBicIssue = true;
                }
            }
        }

        // Retrieve RuleIDs, if they are already set
        List<Payments.RuleID> ruleIDsList = payment.getRuleIDs();
        Payments.RuleID debtorRuleID = null;
        Payments.RuleID creditorRuleID = null;

        // If RuleIDs are not set yet, create them
        if (ruleIDsList == null || ruleIDsList.isEmpty()) {
            debtorRuleID = new Payments.RuleID();
            creditorRuleID = new Payments.RuleID();

            debtorRuleID.setType("Debtor");
            creditorRuleID.setType("Creditor");

            List<String> debtorBankRulesList = new ArrayList<>();
            List<String> creditorBankRulesList = new ArrayList<>();

            // Add bank rules if debtorRuleID is not set already
            if (debtor != null) {
                List<BankRules> bankRulesDebtor = bankRulesRepository.findByRuleType("Debit Side");
                for (BankRules bankRule : bankRulesDebtor) {
                    debtorBankRulesList.add(bankRule.get_id());
                }
            }

            // Add bank rules if creditorRuleID is not set already
            if (creditor != null) {
                List<BankRules> bankRulesCreditor = bankRulesRepository.findByRuleType("Credit Side");
                for (BankRules bankRule : bankRulesCreditor) {
                    creditorBankRulesList.add(bankRule.get_id());
                }
            }

            // Set bank rules to respective RuleIDs
            debtorRuleID.setBank_rules(debtorBankRulesList);
            creditorRuleID.setBank_rules(creditorBankRulesList);

            // Add the RuleIDs to the payment
            ruleIDsList = new ArrayList<>();
            ruleIDsList.add(debtorRuleID);
            ruleIDsList.add(creditorRuleID);

            payment.setRuleIDs(ruleIDsList);  // Set the RuleIDs to the payment object
        } else {
            // If RuleIDs are already set, reuse them
            debtorRuleID = ruleIDsList.stream()
                    .filter(rule -> rule.getType().equals("Debtor"))
                    .findFirst()
                    .orElse(new Payments.RuleID());

            creditorRuleID = ruleIDsList.stream()
                    .filter(rule -> rule.getType().equals("Creditor"))
                    .findFirst()
                    .orElse(new Payments.RuleID());

            // Update only bank rules if needed
            if (debtor != null && debtorRuleID.getBank_rules() == null) {
                List<String> debtorBankRulesList = new ArrayList<>();
                List<BankRules> bankRulesDebtor = bankRulesRepository.findByRuleType("Debit Side");
                for (BankRules bankRule : bankRulesDebtor) {
                    debtorBankRulesList.add(bankRule.get_id());
                }
                debtorRuleID.setBank_rules(debtorBankRulesList);
            }

            if (creditor != null && creditorRuleID.getBank_rules() == null) {
                List<String> creditorBankRulesList = new ArrayList<>();
                List<BankRules> bankRulesCreditor = bankRulesRepository.findByRuleType("Credit Side");
                for (BankRules bankRule : bankRulesCreditor) {
                    creditorBankRulesList.add(bankRule.get_id());
                }
                creditorRuleID.setBank_rules(creditorBankRulesList);
            }
        }

        // Apply Auto Correction for debtor based on rules
        List<AutoCorrectRules> autoCorrectRules = autoCorrectedRulesRepository.findAll();
        for (AutoCorrectRules rule : autoCorrectRules) {
            if (rule.getType().equals("Dbtr") && debtor != null) {
                if (debtor.getDebtorName().equals(rule.getAcctDetails().getName()) &&
                        debtor.getDebtorBic().equals(rule.getAcctDetails().getBIC()) &&
                        debtor.getDebtorIban().equals(rule.getAcctDetails().getIBAN())) {

                    debtor.setDebtorName(rule.getAcctDetails_Fix().getName());
                    debtor.setDebtorBic(rule.getAcctDetails_Fix().getBIC());
                    debtor.setDebtorIban(rule.getAcctDetails_Fix().getIBAN());
                    debtorAutoCorrected = true;
                }
            }
        }

        // Apply Auto Correction for creditor based on rules
        for (AutoCorrectRules rule : autoCorrectRules) {
            if (rule.getType().equals("Cdtr") && creditor != null) {
                if (creditor.getCreditorName().equals(rule.getAcctDetails().getName()) &&
                        creditor.getCreditorBic().equals(rule.getAcctDetails().getBIC()) &&
                        creditor.getCreditorIban().equals(rule.getAcctDetails().getIBAN())) {

                    creditor.setCreditorName(rule.getAcctDetails_Fix().getName());
                    creditor.setCreditorBic(rule.getAcctDetails_Fix().getBIC());
                    creditor.setCreditorIban(rule.getAcctDetails_Fix().getIBAN());
                    creditorAutoCorrected = true;
                }
            }
        }

        // Check if there is a BIC issue (debtor or creditor)
        if (debtorBicIssue || creditorBicIssue) {
            boolean bicAutoCorrectedDb = false;
            boolean bicAutoCorrectedCd = false;

            // Check debtor BIC issue and apply auto-correction if needed
            if (debtorBicIssue && debtorAutoCorrected) {
                // If debtor BIC issue was auto-corrected
                bicAutoCorrectedDb = true;
            }

            // Check creditor BIC issue and apply auto-correction if needed
            if (creditorBicIssue && creditorAutoCorrected) {
                // If creditor BIC issue was auto-corrected
                bicAutoCorrectedCd = true;
            }

            // If either BIC was auto-corrected, set the fileStatus to Auto Corrected
            if (bicAutoCorrectedDb && bicAutoCorrectedCd) {
                payment.setFileStatus("Auto Corrected");
            } else {
                // If BIC issue is not auto-corrected, attempt to find and apply suggestions

                // Handle debtor BIC issue
                if (debtorBicIssue && !debtorAutoCorrected) {
                    String debtorBic = findSuggestedBic(debtor.getDebtorAddress(), debtor.getDebtorIban(), debtor.getDebtorName());
                    String debtorSuggestedBic = extractBicFromJson(debtorBic);
                    if (!"No BIC Found".equals(debtorSuggestedBic)) {
                        debtor.setDebtorBic(debtorSuggestedBic);
                        if (dbtrMlSuggestion == null) {
                            dbtrMlSuggestion = new Payments.MlSuggestion();
                        }
                        dbtrMlSuggestion.setMlSuggestion(debtorSuggestedBic);
                        payment.setDbtrMlSuggestion(dbtrMlSuggestion);
                    }
                }

                // Handle creditor BIC issue
                if (creditorBicIssue && !creditorAutoCorrected) {
                    String creditorBic = findSuggestedBic(creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName());
                    String creditorSuggestedBic = extractBicFromJson(creditorBic);
                    if (!"No BIC Found".equals(creditorSuggestedBic)) {
                        creditor.setCreditorBic(creditorSuggestedBic);
                        if (cdtrMlSuggestion == null) {
                            cdtrMlSuggestion = new Payments.MlSuggestion();
                        }
                        cdtrMlSuggestion.setMlSuggestion(creditorSuggestedBic);
                        payment.setCdtrMlSuggestion(cdtrMlSuggestion);
                    }
                }

                // If no BIC issue is auto-corrected, set status to "To Be Repaired"
                payment.setFileStatus("To Be Repaired");
            }
        } else {
            // If there is no BIC issue, check if auto-correction rules applied
            if (debtorAutoCorrected && creditorAutoCorrected) {
                payment.setFileStatus("Auto Corrected");
            }
        }

//        // If there were corrections, update the XML
//        if (debtorAutoCorrected || creditorAutoCorrected) {
//            try {
//                String updatedXml = createUpdatedXml(payment, debtor, creditor);
//                payment.setUpdatedXml(updatedXml);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // Set the updated RuleIDs to the payment if any changes were made
        List<Payments.RuleID> updatedRuleIDsList = new ArrayList<>();
        updatedRuleIDsList.add(debtorRuleID);
        updatedRuleIDsList.add(creditorRuleID);
        payment.setRuleIDs(updatedRuleIDsList);

        // Return the AutoCorrected status
        Payments.AutoCorrected autoCorrected = new Payments.AutoCorrected();
        autoCorrected.setDebtorAutoCorrected(debtorAutoCorrected);
        autoCorrected.setCreditorAutoCorrected(creditorAutoCorrected);
        return autoCorrected;
    }



    private String findSuggestedBic(String address, String iban, String name) {
        List<String> possibleBicsFromBicRepo = bicRepository.findBicByAddress(address);
        if (!possibleBicsFromBicRepo.isEmpty()) {
            return possibleBicsFromBicRepo.get(0);
        }

        List<String> possibleBicsFromIbanRepo = ibanRepository.findBicByIbanAndName(iban, name);
        if (!possibleBicsFromIbanRepo.isEmpty()) {
            return possibleBicsFromIbanRepo.get(0);
        }

        return "No BIC Found";
    }

    private String createUpdatedXml(Payments payment, Payments.Debtor debtor, Payments.Creditor creditor) throws Exception {
        // Parse the cleaned XML string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(payment.getUpdatedXml()));
        Document document = builder.parse(is);

        // Update debtor details in the XML document
        updateElementTextContent(document, "ns0:Dbtr", "ns0:Nm", debtor.getDebtorName());
        updateElementTextContent(document, "ns0:Dbtr", "ns0:StrtNm", debtor.getDebtorAddress());
        updateElementTextContent(document, "ns0:DbtrAgt", "ns0:BICFI", debtor.getDebtorBic());
        updateElementTextContent(document, "ns0:DbtrAcct", "ns0:Id", debtor.getDebtorIban());

        // Update creditor details in the XML document
        updateElementTextContent(document, "ns0:Cdtr", "ns0:Nm", creditor.getCreditorName());
        updateElementTextContent(document, "ns0:Cdtr", "ns0:StrtNm", creditor.getCreditorAddress());
        updateElementTextContent(document, "ns0:CdtrAgt", "ns0:BICFI", creditor.getCreditorBic());
        updateElementTextContent(document, "ns0:CdtrAcct", "ns0:Id", creditor.getCreditorIban());

        // Create a transformer to generate the XML string from the modified document
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(domSource, streamResult);

        return stringWriter.toString();
    }

    private void updateElementTextContent(Document document, String parentTagName, String childTagName, String newValue) {
        NodeList parentNodes = document.getElementsByTagName(parentTagName);
        if (parentNodes.getLength() > 0) {
            NodeList childNodes = ((Element) parentNodes.item(0)).getElementsByTagName(childTagName);
            if (childNodes.getLength() > 0) {
                childNodes.item(0).setTextContent(newValue);
            }
        }
    }

    private String extractBicFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.path("bic").asText();  // Extract the BIC value
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid BIC";
        }
    }
}

