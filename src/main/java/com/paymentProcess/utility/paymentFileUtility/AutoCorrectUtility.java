package com.paymentProcess.utility.paymentFileUtility;

import com.paymentProcess.entity.AutoCorrectRules;
import com.paymentProcess.entity.BankRules;
import com.paymentProcess.entity.Payments;
import com.paymentProcess.repository.AutoCorrectedRulesRepository;
import com.paymentProcess.repository.BankRulesRepository;
import com.paymentProcess.repository.BicRepository;
import com.paymentProcess.repository.IbanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
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

        List<Payments.RuleID> ruleIDsList = payment.getRuleIDs();
        Payments.RuleID debtorRuleID = null;
        Payments.RuleID creditorRuleID = null;

        if (ruleIDsList == null || ruleIDsList.isEmpty()) {
            debtorRuleID = new Payments.RuleID();
            creditorRuleID = new Payments.RuleID();

            debtorRuleID.setType("Debtor");
            creditorRuleID.setType("Creditor");

            List<String> debtorBankRulesList = new ArrayList<>();
            List<String> creditorBankRulesList = new ArrayList<>();

            if (debtor != null) {
                List<BankRules> bankRulesDebtor = bankRulesRepository.findByRuleType("Debit Side");
                for (BankRules bankRule : bankRulesDebtor) {
                    debtorBankRulesList.add(bankRule.get_id());
                }
            }

            if (creditor != null) {
                List<BankRules> bankRulesCreditor = bankRulesRepository.findByRuleType("Credit Side");
                for (BankRules bankRule : bankRulesCreditor) {
                    creditorBankRulesList.add(bankRule.get_id());
                }
            }

            debtorRuleID.setBank_rules(debtorBankRulesList);
            creditorRuleID.setBank_rules(creditorBankRulesList);

            ruleIDsList = new ArrayList<>();
            ruleIDsList.add(debtorRuleID);
            ruleIDsList.add(creditorRuleID);

            payment.setRuleIDs(ruleIDsList);  // Set the RuleIDs to the payment object
        } else {
            debtorRuleID = ruleIDsList.stream()
                    .filter(rule -> rule.getType().equals("Debtor"))
                    .findFirst()
                    .orElse(new Payments.RuleID());

            creditorRuleID = ruleIDsList.stream()
                    .filter(rule -> rule.getType().equals("Creditor"))
                    .findFirst()
                    .orElse(new Payments.RuleID());

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

        if (debtorBicIssue || creditorBicIssue) {
            boolean bicAutoCorrectedDb = false;
            boolean bicAutoCorrectedCd = false;

            if (debtorBicIssue && debtorAutoCorrected) {
                bicAutoCorrectedDb = true;
            }

            if (creditorBicIssue && creditorAutoCorrected) {
                bicAutoCorrectedCd = true;
            }

            if (bicAutoCorrectedDb && bicAutoCorrectedCd) {
                payment.setFileStatus("Auto Corrected");
            } else {

                if (debtorBicIssue && !debtorAutoCorrected) {
                    String debtorBic = findSuggestedBic(debtor.getDebtorAddress(), debtor.getDebtorIban(), debtor.getDebtorName());
                    String dbBic = payment.getInitialDebtor().getDebtorBic();
                    String debtorSuggestedBic = extractBicFromJson(debtorBic);
                    if (!"No BIC Found".equals(debtorSuggestedBic)) {
                        debtor.setDebtorBic(debtorSuggestedBic);
                        if (dbtrMlSuggestion == null) {
                            dbtrMlSuggestion = new Payments.MlSuggestion();
                        }
                        Double mlScore = getSimilarityScore(dbBic, debtorSuggestedBic);
                        dbtrMlSuggestion.setMlScore(mlScore);
                        dbtrMlSuggestion.setMlSuggestion(debtorSuggestedBic);
                        payment.setDbtrMlSuggestion(dbtrMlSuggestion);
                    }
                }

                if (creditorBicIssue && !creditorAutoCorrected) {
                    String creditorBic = findSuggestedBic(creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName());
                    String creditorSuggestedBic = extractBicFromJson(creditorBic);
                    String CdBic = payment.getInitialCreditor().getCreditorBic();
                    if (!"No BIC Found".equals(creditorSuggestedBic)) {
                        creditor.setCreditorBic(creditorSuggestedBic);
                        if (cdtrMlSuggestion == null) {
                            cdtrMlSuggestion = new Payments.MlSuggestion();
                        }
                        Double mlScore = getSimilarityScore(CdBic, creditorSuggestedBic);
                        cdtrMlSuggestion.setMlScore(mlScore);
                        cdtrMlSuggestion.setMlSuggestion(creditorSuggestedBic);
                        payment.setCdtrMlSuggestion(cdtrMlSuggestion);
                    }
                }

                payment.setFileStatus("To Be Repaired");
            }
        } else {
            if (debtorAutoCorrected && creditorAutoCorrected) {
                payment.setFileStatus("Auto Corrected");
            }
        }

        List<Payments.RuleID> updatedRuleIDsList = new ArrayList<>();
        updatedRuleIDsList.add(debtorRuleID);
        updatedRuleIDsList.add(creditorRuleID);
        payment.setRuleIDs(updatedRuleIDsList);

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

    private String extractBicFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.path("bic").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid BIC";
        }
    }

    public static double getJaroWinklerSimilarityScore(String oldBic, String newBic) {
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        return jaroWinkler.apply(oldBic, newBic);
    }

    // Method to get the similarity score between two strings using Levenshtein Distance
    public static double getSimilarityScore(String oldBic, String newBic) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(oldBic, newBic);
        int maxLength = Math.max(oldBic.length(), newBic.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return 1.0 - (double) distance / maxLength;
    }

    private String createUpdatedXml(Payments payment, Payments.Debtor debtor, Payments.Creditor creditor) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(payment.getUpdatedXml()));
        Document document = builder.parse(is);

        updateElementTextContent(document, "ns0:Dbtr", "ns0:Nm", debtor.getDebtorName());
        updateElementTextContent(document, "ns0:Dbtr", "ns0:StrtNm", debtor.getDebtorAddress());
        updateElementTextContent(document, "ns0:DbtrAgt", "ns0:BICFI", debtor.getDebtorBic());
        updateElementTextContent(document, "ns0:DbtrAcct", "ns0:Id", debtor.getDebtorIban());

        updateElementTextContent(document, "ns0:Cdtr", "ns0:Nm", creditor.getCreditorName());
        updateElementTextContent(document, "ns0:Cdtr", "ns0:StrtNm", creditor.getCreditorAddress());
        updateElementTextContent(document, "ns0:CdtrAgt", "ns0:BICFI", creditor.getCreditorBic());
        updateElementTextContent(document, "ns0:CdtrAcct", "ns0:Id", creditor.getCreditorIban());

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


}

