package com.paymentProcess.utility.paymentFileUtility;

import com.paymentProcess.entity.Payments;
import com.paymentProcess.repository.BicRepository;
import com.paymentProcess.repository.IbanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class BicValidationUtility {

    private final BicRepository bicRepository;
    private final IbanRepository ibanRepository;

    public BicValidationUtility(BicRepository bicRepository, IbanRepository ibanRepository) {
        this.bicRepository = bicRepository;
        this.ibanRepository = ibanRepository;
    }

    public void validateBicAndIban(Payments payment) {
        Payments.Creditor creditor = payment.getCreditor();
        Payments.Debtor debtor = payment.getDebtor();

        List<Payments.BicIssue> bicIssues = new ArrayList<>();
        Payments.BicIssue bicIssue = new Payments.BicIssue();

        String suggestBicValue = "";

        if (!isBicValid(debtor.getDebtorBic(), debtor.getDebtorAddress(),debtor.getDebtorIban(), debtor.getDebtorName())) {
            suggestBicValue = findSuggestedBic(debtor.getDebtorAddress(), debtor.getDebtorIban(), debtor.getDebtorName());
            String extractedBic = extractBicFromJson(suggestBicValue);
            setMlSuggestionForDebtor(payment, extractedBic);

            bicIssue.setDebtorBicIssue(true);

        }

        if (!isBicValid(creditor.getCreditorBic(), creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName())) {
            suggestBicValue = findSuggestedBic(creditor.getCreditorAddress(), creditor.getCreditorIban(), creditor.getCreditorName());
            String extractedBic = extractBicFromJson(suggestBicValue);
            setMlSuggestionForCreditor(payment, extractedBic);

            bicIssue.setCreditorBicIssue(true);

        }
        bicIssues.add(bicIssue);
        payment.setBicIssues(bicIssues);

        if (bicIssue.isCreditorBicIssue() || bicIssue.isDebtorBicIssue()) {
            payment.setToBeEdited(true);
            payment.setFileStatus("To Be Repaired");
        }else {
            payment.setFileStatus("STP");
        }
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

    private boolean isBicValid(String bic, String address, String iban, String name) {
        boolean isBicFound = false;

        if (address != null) {
            isBicFound = bicRepository.existsByBicAndAddress(bic, address);
        } else if (iban != null && name != null) {
            isBicFound = ibanRepository.findBicByIbanAndName(iban, name) != null;
        }

        if (isBicFound) {
            return true;
        }
        if (!isValidBicSyntax(bic)) {
            return false;
        }

        return false;
    }


    private boolean isValidBicSyntax(String bic) {
        String bicRegex = "^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}[A-Z0-9]{3}$";
        Pattern pattern = Pattern.compile(bicRegex);
        return pattern.matcher(bic).matches();
    }

    private void setMlSuggestionForDebtor(Payments payment, String suggestBicValue) {
        Payments.MlSuggestion dbtrMlSuggestion = new Payments.MlSuggestion();
        dbtrMlSuggestion.setMlSuggestion(suggestBicValue);
        payment.setDbtrMlSuggestion(dbtrMlSuggestion);
    }

    private void setMlSuggestionForCreditor(Payments payment, String suggestBicValue) {
        Payments.MlSuggestion cdtrMlSuggestion = new Payments.MlSuggestion();
        cdtrMlSuggestion.setMlSuggestion(suggestBicValue);
        payment.setCdtrMlSuggestion(cdtrMlSuggestion);
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
}

