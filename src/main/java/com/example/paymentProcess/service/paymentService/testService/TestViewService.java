//package com.example.demo.utility.paymentFileUtility;
//
//import com.example.demo.entity.Payments;
//import com.example.demo.repository.BicRepository;
//import com.example.demo.repository.IbanRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//@Component
//public class BicValidationUtility {
//
//    private final BicRepository bicRepository;
//    private final IbanRepository ibanRepository;
//
//    public BicValidationUtility(BicRepository bicRepository, IbanRepository ibanRepository) {
//        this.bicRepository = bicRepository;
//        this.ibanRepository = ibanRepository;
//    }
//
//
//    public void validateBicAndIban(Payments payment) {
//        Payments.Creditor creditor = payment.getCreditor();
//        Payments.Debtor debtor = payment.getDebtor();
//
//        List<Payments.BicIssue> bicIssues = new ArrayList<>();
//        Payments.BicIssue bicIssue = new Payments.BicIssue();
//
//        String suggestBicValue = "";  // Variable to store suggested BIC value
//
//        // Validate Debtor BIC
//        if (!isBicValid(debtor.getDebtorBic(), debtor.getDebtorAddress(),debtor.getDebtorIban(), debtor.getDebtorName())) {
//            // Search for the correct BIC if not found in the BIC repository
//            suggestBicValue = findSuggestedBic(debtor.getDebtorAddress(), debtor.getDebtorIban(), debtor.getDebtorName());
//            // Extract BIC from the suggestBicValue (Assuming it contains JSON string)
//            String extractedBic = extractBicFromJson(suggestBicValue);
//            // Set the extracted BIC for Debtor
//            setMlSuggestionForDebtor(payment, extractedBic);
//
//            payment.getDebtor().setDebtorBic(extractedBic);
//            debtor.setDebtorBic(extractedBic);
//
//        }
//
//        // Validate Creditor BIC
//        if (!isBicValid(creditor.getCreditorBic(), creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName())) {
////             Search for the correct BIC if not found in the BIC repository
//            suggestBicValue = findSuggestedBic(creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName());
//            // Extract BIC from the suggestBicValue (Assuming it contains JSON string)
//            String extractedBic = extractBicFromJson(suggestBicValue);
//            // Set the extracted BIC for Creditor
//            setMlSuggestionForCreditor(payment, extractedBic);
//
//            creditor.setCreditorBic(extractedBic);
//
//        }
//
//        bicIssues.add(bicIssue);
//        payment.setBicIssues(bicIssues);
//
//        if (bicIssue.isCreditorBicIssue() || bicIssue.isDebtorBicIssue()) {
//            payment.setToBeEdited(true);
//            payment.setFileStatus("To Be Repaired");
//        }else {
//            payment.setFileStatus("STP");
//        }
//    }
//
//    // Helper method to extract BIC value from JSON string
//    private String extractBicFromJson(String json) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(json);
//            return jsonNode.path("bic").asText(); // Extract the BIC value
//        } catch (Exception e) {
//            // Handle JSON parsing exceptions
//            e.printStackTrace();
//            return "Invalid BIC";  // Return a default invalid value if parsing fails
//        }
//    }
//
//    // Helper method to validate BIC by checking the BicRepository and syntax
//    private boolean isBicValid(String bic, String address, String iban, String name) {
//        boolean isBicFound = false;
//
//        // If address is provided, check BIC and address in the BicRepository
//        if (address != null) {
//            isBicFound = bicRepository.existsByBicAndAddress(bic, address);
//        } else if (iban != null && name != null) {
//            // If address is null, check BIC by IBAN and name
//            isBicFound = ibanRepository.findBicByIbanAndName(iban, name) != null;
//        }
//
//        // If BIC is found in the repository, return true
//        if (isBicFound) {
//            return true;
//        }
//
//        // If not found in repository, validate BIC format using a regex pattern
//        if (!isValidBicSyntax(bic)) {
//            return false;
//        }
//
//        return false;
//    }
//
//
//    // Helper method to check if the BIC follows the standard syntax
//    private boolean isValidBicSyntax(String bic) {
//        // Example BIC pattern: It should be 8 or 11 characters long, and only alphanumeric characters
//        String bicRegex = "^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}[A-Z0-9]{3}$";
//        Pattern pattern = Pattern.compile(bicRegex);
//        return pattern.matcher(bic).matches();
//    }
//
//    // Helper method to set the MlSuggestion for Debtor with the found BIC or default value
//    private void setMlSuggestionForDebtor(Payments payment, String suggestBicValue) {
//        Payments.MlSuggestion dbtrMlSuggestion = new Payments.MlSuggestion();
//        dbtrMlSuggestion.setMlSuggestion(suggestBicValue);  // Set the found BIC or default message
//        payment.setDbtrMlSuggestion(dbtrMlSuggestion);
//    }
//
//    // Helper method to set the MlSuggestion for Creditor with the found BIC or default value
//    private void setMlSuggestionForCreditor(Payments payment, String suggestBicValue) {
//        Payments.MlSuggestion cdtrMlSuggestion = new Payments.MlSuggestion();
//        cdtrMlSuggestion.setMlSuggestion(suggestBicValue);  // Set the found BIC or default message
//        payment.setCdtrMlSuggestion(cdtrMlSuggestion);
//    }
//
//    // Helper method to find suggested BIC from BIC repository by address, or from IBAN repository by IBAN and name
//    private String findSuggestedBic(String address, String iban, String name) {
//        // First try to find a BIC by just using the address in the BIC repository
//        List<String> possibleBicsFromBicRepo = bicRepository.findBicByAddress(address);
//        if (!possibleBicsFromBicRepo.isEmpty()) {
//            return possibleBicsFromBicRepo.get(0);  // Return the first suggested BIC from BIC repository
//        }
//
//        // If no match in the BIC repository, try finding it from the IbanRepository using IBAN and name
//        List<String> possibleBicsFromIbanRepo = ibanRepository.findBicByIbanAndName(iban, name);
//        if (!possibleBicsFromIbanRepo.isEmpty()) {
//            return possibleBicsFromIbanRepo.get(0);  // Return the first suggested BIC from IBAN repository
//        }
//
//        // Return a default message if no suggestion is found
//        return "No BIC Found";  // This is just a placeholder, you can adjust this to something else if needed
//    }
//}
//
