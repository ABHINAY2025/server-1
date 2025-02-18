package com.paymentProcess.utility;

import com.paymentProcess.entity.AutoCorrectRules;
import com.paymentProcess.entity.Payments;
import com.paymentProcess.repository.AutoCorrectedRulesRepository;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Component
public class ApprovedUtility {

    private final AutoCorrectedRulesRepository autoCorrectedRulesRepository;

    public ApprovedUtility(AutoCorrectedRulesRepository autoCorrectedRulesRepository) {
        this.autoCorrectedRulesRepository = autoCorrectedRulesRepository;
    }

    public void createAndSaveAutoCorrectRules(Payments payments) {
        String updatedXml = payments.getUpdatedXml();

        AutoCorrectRules dbtrRule = createAutoCorrectRules(payments, updatedXml, "Dbtr");
        autoCorrectedRulesRepository.save(dbtrRule);

        AutoCorrectRules cdtrRule = createAutoCorrectRules(payments, updatedXml, "Cdtr");
        autoCorrectedRulesRepository.save(cdtrRule);
    }

    public AutoCorrectRules createAutoCorrectRules(Payments paymentFile, String updatedXml, String type) {
        AutoCorrectRules autoCorrectRules = new AutoCorrectRules();
        autoCorrectRules.setMessageInfoId(paymentFile.getMsgId());

        if (type.equals("Dbtr")) {
            autoCorrectRules.setType("Dbtr");
            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Dbtr"));
            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(paymentFile.getUpdatedXml(), "Dbtr"));
        } else if (type.equals("Cdtr")) {
            autoCorrectRules.setType("Cdtr");
            autoCorrectRules.setAcctDetails(extractAcctDetails(paymentFile.getOriginalXml(), "Cdtr"));
            autoCorrectRules.setAcctDetails_Fix(extractAcctDetails(paymentFile.getUpdatedXml(), "Cdtr"));
        }

        return autoCorrectRules;
    }

    public AutoCorrectRules.AcctDetails extractAcctDetails(String xml, String type) {
        if (xml == null || xml.isEmpty()) {
            System.err.println("Error: XML string is null or empty for type " + type);
            return null;  // Return null if XML is invalid
        }
        Document document = parseXml(xml);

        if (document == null) {
            System.err.println("Error: Failed to parse XML for type " + type);
            return null;
        }

        AutoCorrectRules.AcctDetails acctDetails = new AutoCorrectRules.AcctDetails();

        if (type.equals("Dbtr")) {
            // Extract Debtor (Dbtr) account details
            NodeList debtorNameNodes = document.getElementsByTagName("ns0:Dbtr");
            if (debtorNameNodes.getLength() > 0) {
                NodeList debtorNameNodeList = ((org.w3c.dom.Element) debtorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
                if (debtorNameNodeList.getLength() > 0) {
                    acctDetails.setName(cleanUpString(debtorNameNodeList.item(0).getTextContent()));  // Set the debtor name
                }
            }

            NodeList debtorBicNodes = document.getElementsByTagName("ns0:DbtrAgt");
            if (debtorBicNodes.getLength() > 0) {
                Node debtorBicNode = ((org.w3c.dom.Element) debtorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
                if (debtorBicNode != null) {
                    acctDetails.setBIC(cleanUpString(debtorBicNode.getTextContent()));  // Set the debtor BIC
                }
            }

            NodeList debtorIbanNodes = document.getElementsByTagName("ns0:DbtrAcct");
            if (debtorIbanNodes.getLength() > 0) {
                Node debtorIbanNode = ((org.w3c.dom.Element) debtorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
                if (debtorIbanNode != null) {
                    acctDetails.setIBAN(cleanUpString(debtorIbanNode.getTextContent()));  // Set the debtor IBAN
                }
            }

        } else if (type.equals("Cdtr")) {
            NodeList creditorNameNodes = document.getElementsByTagName("ns0:Cdtr");
            if (creditorNameNodes.getLength() > 0) {
                NodeList creditorNameNodeList = ((org.w3c.dom.Element) creditorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
                if (creditorNameNodeList.getLength() > 0) {
                    acctDetails.setName(cleanUpString(creditorNameNodeList.item(0).getTextContent()));  // Set the creditor name
                }
            }

            NodeList creditorBicNodes = document.getElementsByTagName("ns0:CdtrAgt");
            if (creditorBicNodes.getLength() > 0) {
                Node creditorBicNode = ((org.w3c.dom.Element) creditorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
                if (creditorBicNode != null) {
                    acctDetails.setBIC(cleanUpString(creditorBicNode.getTextContent()));  // Set the creditor BIC
                }
            }

            NodeList creditorIbanNodes = document.getElementsByTagName("ns0:CdtrAcct");
            if (creditorIbanNodes.getLength() > 0) {
                Node creditorIbanNode = ((org.w3c.dom.Element) creditorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
                if (creditorIbanNode != null) {
                    acctDetails.setIBAN(cleanUpString(creditorIbanNode.getTextContent()));  // Set the creditor IBAN
                }
            }
        }
        acctDetails.setCategory("BIC");

        return acctDetails;
    }

    public String cleanUpString(String input) {
        return input != null ? input.trim() : "";
    }

    public Document parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(xml));
            return builder.parse(inputSource);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
