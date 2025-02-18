package com.paymentProcess.utility;

import com.paymentProcess.entity.Payments;
import com.paymentProcess.utility.paymentFileUtility.AutoCorrectUtility;
import com.paymentProcess.utility.paymentFileUtility.BicValidationUtility;
import com.paymentProcess.utility.paymentFileUtility.StpConfigurationUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PaymentProcessUtility {

    private final AutoCorrectUtility autoCorrectUtility;
    private final StpConfigurationUtility stpConfigurationUtility;
    private final BicValidationUtility bicValidationUtility;

    public PaymentProcessUtility(AutoCorrectUtility autoCorrectUtility, StpConfigurationUtility stpConfigurationUtility, BicValidationUtility bicValidationUtility) {
        this.autoCorrectUtility = autoCorrectUtility;
        this.stpConfigurationUtility = stpConfigurationUtility;
        this.bicValidationUtility = bicValidationUtility;
    }

    private String formatXml(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource domSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(domSource, streamResult);

        return stringWriter.toString();
    }

    public Payments ExtractPayment(String jsonFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonFile);
        String originalXml = rootNode.get("originalXml").asText();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream is = new ByteArrayInputStream(originalXml.getBytes("UTF-8"));
        Document document = builder.parse(is);

        String formattedOriginalXml = formatXml(document);

        Payments payment = new Payments();
        payment.setOriginalXml(formattedOriginalXml);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setToBeEdited(false);

        Payments.Debtor debtor = extractDebtorDetails(document);
        payment.setDebtor(debtor);

        Payments.Creditor creditor = extractCreditorDetails(document);
        payment.setCreditor(creditor);

        Payments.Debtor initialDebtor = extractDebtorDetails(document);
        payment.setInitialDebtor(initialDebtor);

        Payments.Creditor initialCreditor = extractCreditorDetails(document);
        payment.setInitialCreditor(initialCreditor);

        NodeList amountNodes = document.getElementsByTagName("ns0:IntrBkSttlmAmt");
        if (amountNodes.getLength() > 0) {
            String amountStr = amountNodes.item(0).getTextContent();
            Double amount = Double.valueOf(amountStr);
            payment.setAmount(amount);
        }

        NodeList msgIdNodes = document.getElementsByTagName("ns0:MsgId");
        if (msgIdNodes.getLength() > 0) {
            payment.setMsgId(msgIdNodes.item(0).getTextContent());
        }

        payment.setFileStatus("Received");
        stpConfigurationUtility.applyStpConfigurations(payment);

        String updatedXml = createUpdatedXml(document, payment.getDebtor(), payment.getCreditor());
        payment.setUpdatedXml(updatedXml);

        bicValidationUtility.validateBicAndIban(payment);

        log.info("paymentFile : {}", payment);

        if(!"STP".equals(payment.getFileStatus())){
            Payments.AutoCorrected autoCorrected = autoCorrectUtility.compareAndCorrect(payment);
            List<Payments.AutoCorrected> autoCorrecteds = new ArrayList<>();
            autoCorrecteds.add(autoCorrected);
            payment.setAutoCorrected(autoCorrecteds);

            String updatedXml2 = createUpdatedXml(document, payment.getDebtor(), payment.getCreditor());
            payment.setUpdatedXml(updatedXml2);
        }

        String finalUpdatedXml = createUpdatedXml(document, payment.getDebtor(), payment.getCreditor());
        payment.setUpdatedXml(finalUpdatedXml);

        return payment;
    }

    public Payments.Debtor extractDebtorDetails(Document document) {
        Payments.Debtor debtor = new Payments.Debtor();
        NodeList debtorNameNodes = document.getElementsByTagName("ns0:Dbtr");
        if (debtorNameNodes.getLength() > 0) {
            NodeList debtorNameNodeList = ((Element) debtorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
            if (debtorNameNodeList.getLength() > 0) {
                debtor.setDebtorName(cleanUpString(debtorNameNodeList.item(0).getTextContent()));
            }
        }

        NodeList debtorAddressNodes = document.getElementsByTagName("ns0:Dbtr");
        if (debtorAddressNodes.getLength() > 0) {
            Node debtorNode = debtorAddressNodes.item(0);
            StringBuilder debtorAddress = new StringBuilder();

            NodeList debtorStreetNameNodes = ((Element) debtorNode).getElementsByTagName("ns0:StrtNm");
            for (int i = 0; i < debtorStreetNameNodes.getLength(); i++) {
                Node streetNode = debtorStreetNameNodes.item(i);
                if (streetNode != null) {
                    debtorAddress.append(cleanUpString(streetNode.getTextContent())).append(" ");
                }
            }

            NodeList debtorTownNameNodes = ((Element) debtorNode).getElementsByTagName("ns0:TwnNm");
            for (int i = 0; i < debtorTownNameNodes.getLength(); i++) {
                Node townNode = debtorTownNameNodes.item(i);
                if (townNode != null) {
                    debtorAddress.append(cleanUpString(townNode.getTextContent())).append(" ");
                }
            }

            debtor.setDebtorAddress(debtorAddress.toString().trim());
        }

        NodeList debtorBicNodes = document.getElementsByTagName("ns0:DbtrAgt");
        if (debtorBicNodes.getLength() > 0) {
            Node debtorBicNode = ((Element) debtorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
            if (debtorBicNode != null) {
                debtor.setDebtorBic(cleanUpString(debtorBicNode.getTextContent()));
            }
        }

        NodeList debtorIbanNodes = document.getElementsByTagName("ns0:DbtrAcct");
        if (debtorIbanNodes.getLength() > 0) {
            Node debtorIbanNode = ((Element) debtorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
            if (debtorIbanNode != null) {
                debtor.setDebtorIban(cleanUpString(debtorIbanNode.getTextContent()));
            }
        }

        return debtor;
    }

    public Payments.Creditor extractCreditorDetails(Document document) {
        Payments.Creditor creditor = new Payments.Creditor();
        NodeList creditorNameNodes = document.getElementsByTagName("ns0:Cdtr");
        if (creditorNameNodes.getLength() > 0) {
            NodeList creditorNameNodeList = ((Element) creditorNameNodes.item(0)).getElementsByTagName("ns0:Nm");
            if (creditorNameNodeList.getLength() > 0) {
                creditor.setCreditorName(cleanUpString(creditorNameNodeList.item(0).getTextContent()));
            }
        }

        NodeList creditorAddressNodes = document.getElementsByTagName("ns0:Cdtr");
        if (creditorAddressNodes.getLength() > 0) {
            Node creditorNode = creditorAddressNodes.item(0);
            StringBuilder creditorAddress = new StringBuilder();

            NodeList streetNameNodes = ((Element) creditorNode).getElementsByTagName("ns0:StrtNm");
            for (int i = 0; i < streetNameNodes.getLength(); i++) {
                Node streetNode = streetNameNodes.item(i);
                if (streetNode != null) {
                    creditorAddress.append(cleanUpString(streetNode.getTextContent())).append(" ");
                }
            }

            NodeList townNameNodes = ((Element) creditorNode).getElementsByTagName("ns0:TwnNm");
            for (int i = 0; i < townNameNodes.getLength(); i++) {
                Node townNode = townNameNodes.item(i);
                if (townNode != null) {
                    creditorAddress.append(cleanUpString(townNode.getTextContent())).append(" ");
                }
            }

            creditor.setCreditorAddress(creditorAddress.toString().trim());
        }

        NodeList creditorBicNodes = document.getElementsByTagName("ns0:CdtrAgt");
        if (creditorBicNodes.getLength() > 0) {
            Node creditorBicNode = ((Element) creditorBicNodes.item(0)).getElementsByTagName("ns0:BICFI").item(0);
            if (creditorBicNode != null) {
                creditor.setCreditorBic(cleanUpString(creditorBicNode.getTextContent()));
            }
        }

        NodeList creditorIbanNodes = document.getElementsByTagName("ns0:CdtrAcct");
        if (creditorIbanNodes.getLength() > 0) {
            Node creditorIbanNode = ((Element) creditorIbanNodes.item(0)).getElementsByTagName("ns0:Id").item(0);
            if (creditorIbanNode != null) {
                creditor.setCreditorIban(cleanUpString(creditorIbanNode.getTextContent()));
            }
        }

        return creditor;
    }

    public String cleanUpString(String input) {
        return input.replaceAll("\\n", "").replaceAll("\\r", "").trim();
    }

    private String createUpdatedXml(Document document, Payments.Debtor debtor, Payments.Creditor creditor) throws Exception {
        updateElementTextContent(document, "ns0:Dbtr", "ns0:Nm", debtor.getDebtorName());
        updateElementTextContent(document, "ns0:Dbtr", "ns0:StrtNm", debtor.getDebtorAddress());
        updateElementTextContent(document, "ns0:DbtrAgt", "ns0:BICFI", debtor.getDebtorBic());

        updateIbanElement(document, "ns0:DbtrAcct", debtor.getDebtorIban());

        updateElementTextContent(document, "ns0:Cdtr", "ns0:Nm", creditor.getCreditorName());
        updateElementTextContent(document, "ns0:Cdtr", "ns0:StrtNm", creditor.getCreditorAddress());
        updateElementTextContent(document, "ns0:CdtrAgt", "ns0:BICFI", creditor.getCreditorBic());

        updateIbanElement(document, "ns0:CdtrAcct", creditor.getCreditorIban());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource domSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        transformer.transform(domSource, streamResult);

        return stringWriter.toString();
    }

    private void updateIbanElement(Document document, String parentTagName, String ibanValue) {
        NodeList accountNodes = document.getElementsByTagName(parentTagName);
        if (accountNodes.getLength() > 0) {
            Element accountElement = (Element) accountNodes.item(0);

            NodeList idNodes = accountElement.getElementsByTagName("ns0:Id");
            Element idElement;
            if (idNodes.getLength() == 0) {
                idElement = document.createElement("ns0:Id");
                accountElement.appendChild(idElement);
            } else {
                idElement = (Element) idNodes.item(0);
            }

            NodeList othrNodes = idElement.getElementsByTagName("ns0:Othr");
            Element othrElement;
            if (othrNodes.getLength() == 0) {
                othrElement = document.createElement("ns0:Othr");
                idElement.appendChild(othrElement);
            } else {
                othrElement = (Element) othrNodes.item(0);
            }

            NodeList existingIdNodes = othrElement.getElementsByTagName("ns0:Id");
            Element innerIdElement;
            if (existingIdNodes.getLength() == 0) {
                innerIdElement = document.createElement("ns0:Id");
                othrElement.appendChild(innerIdElement);
            } else {
                innerIdElement = (Element) existingIdNodes.item(0);
            }

            innerIdElement.setTextContent(ibanValue);
        }
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
