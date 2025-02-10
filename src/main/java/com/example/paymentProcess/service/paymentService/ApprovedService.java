package com.example.paymentProcess.service.paymentService;

import com.example.paymentProcess.entity.Payments;
import com.example.paymentProcess.repository.PaymentsRepository;
import com.example.paymentProcess.utility.ApprovedUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

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

@Service
public class ApprovedService {

    private final PaymentsRepository paymentsRepository;
    private final ApprovedUtility approvedUtility;

    public ApprovedService(PaymentsRepository paymentsRepository, ApprovedUtility approvedUtility) {
        this.paymentsRepository = paymentsRepository;
        this.approvedUtility = approvedUtility;
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

    // Method to approve the payment file
    public boolean approvePaymentFile(String id, String updatedXml) throws Exception {
        // Fetch the PaymentFile by ID
        Payments payments = paymentsRepository.findById(id).orElse(null);
        if (payments != null) {

            // Use Jackson to parse the JSON string and extract the XML part
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(updatedXml);
            String originalXml = rootNode.get("updatedXml").asText();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(originalXml.getBytes("UTF-8"));
            Document document = builder.parse(is);

            String formattedOriginalXml = formatXml(document);

//            // Remove BOM (Byte Order Mark) if it exists and trim the string
//            if (originalXml.startsWith("\uFEFF")) {
//                originalXml = originalXml.substring(1);
//            }

//            originalXml = originalXml.trim();

            // Set the updatedXml as a String
            payments.setUpdatedXml(formattedOriginalXml);
            payments.setFileStatus("Approved");

            // Save the updated PaymentFile
            paymentsRepository.save(payments);

            // Create and save AutoCorrectRules records for debtor and creditor
            approvedUtility.createAndSaveAutoCorrectRules(payments);

            return true;
        } else {
            return false;
        }
    }

}