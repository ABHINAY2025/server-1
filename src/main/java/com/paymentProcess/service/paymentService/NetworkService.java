package com.paymentProcess.service.paymentService;

import com.paymentProcess.entity.Networks;
import com.paymentProcess.repository.NetworksRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;

@Service
public class NetworkService {

    @Autowired
    private NetworksRepository networksRepository;

//    public void readNetworkMessage(String xml) throws JAXBException, JsonProcessingException {
//        // Convert XML to Java Object using JAXB
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = objectMapper.readTree(xml);
//        String networkMsg = rootNode.get("xmlPayload").asText();
//
//        JAXBContext context = JAXBContext.newInstance(FedNowMessage.class);
//        FedNowMessage fedNowMessage = (FedNowMessage) context.createUnmarshaller().unmarshal(new StringReader(networkMsg));
//
//        String messageID = fedNowMessage.getMessageIdentification().getMessageID();
//        String additionalInfo = fedNowMessage.getStatusReasonInformation().getAdditionalInformation();
//
//        if (messageID == null || additionalInfo == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields in the XML message.");
//        }
//
//        // Determine status based on AdditionalInformation content
//        String statusCode;
//        if (additionalInfo.toLowerCase().contains("downtime") || additionalInfo.toLowerCase().contains("outage")) {
//            statusCode = "Down";
//        } else if (additionalInfo.toLowerCase().contains("operational")) {
//            statusCode = "Active";
//        } else {
//            System.out.println("Unrecognized status in AdditionalInformation.");
//            return; // Exit early if the status is not actionable
//        }
//
//        // Fetch and update networks
//        List<Networks> networkDetailsList = networksRepository.findByCompanyName("Wells Fargo & Co.");
//        if (networkDetailsList == null || networkDetailsList.isEmpty()) {
//            System.out.println("No networks found for the specified company.");
//            return;
//        }
//
//        // Update the network status
//        for (Networks network : networkDetailsList) {
//            if (messageID.contains(network.getName())) {
//                network.setStatus(statusCode);
//                network.setAdditionalInfo(additionalInfo);
//                networksRepository.save(network);
//                System.out.println("Network " + network.getName() + " status updated");
//            }
//        }
//    }

    public void readNetworkMessage(String xml) throws Exception {
        try {
            // Parse the JSON payload to extract the XML part
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(xml);
            String networkMsg = rootNode.get("xmlPayload").asText();  // Extract XML string from JSON

            // Now, parse the XML string using DOM (Document Object Model)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(networkMsg)));

            // Extract the messageID from the XML
            String messageID = getTagValue(document, "MessageID");

            // Extract the AdditionalInformation from the XML
            String additionalInfo = getTagValue(document, "AdditionalInformation");

            // Validate mandatory fields
            if (messageID == null || additionalInfo == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields in the XML message.");
            }

            // Determine status based on AdditionalInformation content
            String statusCode = getStatusCode(additionalInfo);

            // Fetch primary networks
            List<Networks> primaryNetworks = networksRepository.findByType("Primary");
            if (primaryNetworks == null || primaryNetworks.isEmpty()) {
                System.out.println("No primary network found.");
                return;
            }

            // Update primary network status if MessageID matches
            boolean networkUpdated = false;
            for (Networks network : primaryNetworks) {
                if (messageID.contains(network.getName())) {
                    network.setStatus(statusCode);
                    network.setAdditionalInfo(additionalInfo);
                    networksRepository.save(network);  // Save the updated network record
                    System.out.println("Network " + network.getName() + " status updated.");
                    networkUpdated = true;
                    break;  // Assuming only one match, exit loop early
                }
            }

            // If statusCode is Active, update the secondary networks
            if ("Active".equals(statusCode) && networkUpdated) {
                updateSecondaryNetworks();
            } else if (!networkUpdated) {
                System.out.println("No matching primary network found for MessageID.");
            }

        } catch (Exception e) {
            // Log the error and throw a meaningful exception
            System.err.println("Error processing network message: " + e.getMessage());
            throw new Exception("Error processing network message", e);
        }
    }

    private String getTagValue(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : null;
    }

    private String getStatusCode(String additionalInfo) {
        if (additionalInfo.toLowerCase().contains("downtime") || additionalInfo.toLowerCase().contains("outage")) {
            return "Down";
        } else if (additionalInfo.toLowerCase().contains("operational")) {
            return "Active";
        } else {
            System.out.println("Unrecognized status in AdditionalInformation.");
            return null;
        }
    }

    private void updateSecondaryNetworks() {
        List<Networks> secondaryNetworks = networksRepository.findByType("Secondary");
        if (secondaryNetworks != null && !secondaryNetworks.isEmpty()) {
            for (Networks secondaryNetwork : secondaryNetworks) {
                // Check if isApprove is true, then set it to false
                if (secondaryNetwork.isApproved()) {
                    secondaryNetwork.setApproved(false);
                    networksRepository.save(secondaryNetwork);  // Save the updated secondary network
                    System.out.println("Secondary network " + secondaryNetwork.getName() + " isApprove updated to false.");
                } else {
                    System.out.println("Secondary network " + secondaryNetwork.getName() + " isApprove is already false.");
                }
            }
        } else {
            System.out.println("No secondary networks found.");
        }
    }


    // Method to update the approval status of a network
    public Networks updateApprovedStatus(String networkId, boolean isApproved) {
        // Find the network by its ID
        Networks network = networksRepository.findById(networkId).orElse(null);

        if (network != null) {
            // Update the 'isApproved' field
            network.setApproved(isApproved);
            // Save the updated network back to the database
            return networksRepository.save(network);
        }
        return null;  // Return null if network is not found
    }
}

