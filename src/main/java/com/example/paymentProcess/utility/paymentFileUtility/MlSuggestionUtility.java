//package com.example.paymentProcess.utility.paymentFileUtility;
//
//import com.example.paymentProcess.entity.BicTable;
//import com.example.paymentProcess.entity.IbanTable;
//import com.example.paymentProcess.entity.Payments;
//import com.example.paymentProcess.repository.BicRepository;
//import com.example.paymentProcess.repository.IbanRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class MlSuggestionUtility {
//
//    private final BicRepository bicRepository;
//    private final IbanRepository ibanRepository;
//
//    public MlSuggestionUtility(BicRepository bicRepository, IbanRepository ibanRepository) {
//        this.bicRepository = bicRepository;
//        this.ibanRepository = ibanRepository;
//    }
//
//    public void mlsuggestion(Payments paymentFile) {
//        // Get the creditor and debtor objects from the paymentFile
//        Payments.Creditor creditor = paymentFile.getCreditor();
//        Payments.Debtor debtor = paymentFile.getDebtor();
//
//        // Validate for Creditor first
//        if (creditor != null) {
//            // Find the BIC and Address for the Creditor in BicTable
//            Optional<BicTable> bicTable = bicRepository.findByBicAndAddress(creditor.getCreditorBic(), creditor.getCreditorAddress());
//            if (bicTable.isPresent()) {
//                // BIC found in BicTable, set the suggestion for Creditor
//                Payments.MlSuggestion cdtrMlSuggestion = new Payments.MlSuggestion();
//                cdtrMlSuggestion.setMlScore(1.0);  // Example score
//                cdtrMlSuggestion.setMlSuggestion("Valid BIC in BicTable");
//                paymentFile.setCdtrMlSuggestion(cdtrMlSuggestion);
//            } else {
//                // BIC not found in BicTable, validate with IbanTable
//                Optional<IbanTable> ibanTable = ibanRepository.findByBicAndIbanAndName(creditor.getCreditorBic(), creditor.getCreditorIban(), creditor.getCreditorName());
//                if (ibanTable.isPresent()) {
//                    // BIC found in IbanTable, set the suggestion for Creditor
//                    Payments.MlSuggestion cdtrMlSuggestion = new Payments.MlSuggestion();
//                    cdtrMlSuggestion.setMlScore(0.8);  // Example score
//                    cdtrMlSuggestion.setMlSuggestion("Valid BIC in IbanTable");
//                    paymentFile.setCdtrMlSuggestion(cdtrMlSuggestion);
//                } else {
//                    // No BIC found in both tables, set suggestion as invalid
//                    Payments.MlSuggestion cdtrMlSuggestion = new Payments.MlSuggestion();
//                    cdtrMlSuggestion.setMlScore(0.0);  // Example score
//                    cdtrMlSuggestion.setMlSuggestion("Invalid BIC");
//                    paymentFile.setCdtrMlSuggestion(cdtrMlSuggestion);
//                }
//            }
//        }
//
//        // Validate for Debtor if Creditor is not found or both are separate entities
//        if (debtor != null) {
//            // Find the BIC and Address for the Debtor in BicTable
//            Optional<BicTable> bicTable = bicRepository.findByBicAndAddress(debtor.getDebtorBic(), debtor.getDebtorAddress());
//            if (bicTable.isPresent()) {
//                // BIC found in BicTable, set the suggestion for Debtor
//                Payments.MlSuggestion dbtrMlSuggestion = new Payments.MlSuggestion();
//                dbtrMlSuggestion.setMlScore(1.0);  // Example score
//                dbtrMlSuggestion.setMlSuggestion("Valid BIC in BicTable");
//                paymentFile.setDbtrMlSuggestion(dbtrMlSuggestion);
//            } else {
//                // BIC not found in BicTable, validate with IbanTable
//                Optional<IbanTable> ibanTable = ibanRepository.findByBicAndIbanAndName(debtor.getDebtorBic(), debtor.getDebtorIban(), debtor.getDebtorName());
//                if (ibanTable.isPresent()) {
//                    // BIC found in IbanTable, set the suggestion for Debtor
//                    Payments.MlSuggestion dbtrMlSuggestion = new Payments.MlSuggestion();
//                    dbtrMlSuggestion.setMlScore(0.8);  // Example score
//                    dbtrMlSuggestion.setMlSuggestion("Valid BIC in IbanTable");
//                    paymentFile.setDbtrMlSuggestion(dbtrMlSuggestion);
//                } else {
//                    // No BIC found in both tables, set suggestion as invalid
//                    Payments.MlSuggestion dbtrMlSuggestion = new Payments.MlSuggestion();
//                    dbtrMlSuggestion.setMlScore(0.0);  // Example score
//                    dbtrMlSuggestion.setMlSuggestion("Invalid BIC");
//                    paymentFile.setDbtrMlSuggestion(dbtrMlSuggestion);
//                }
//            }
//        }
//    }
//
//
//}
