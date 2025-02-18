//package com.example.demo.controller;
//
//import com.example.demo.service.servic.ViewDataService;
//import com.example.demo.views.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/views")
//public class ViewDataController {
//
//
//    @Autowired
//    private ViewDataService viewDataService;
//
//    @GetMapping("/stp")
//    public ResponseEntity<STPView> getSTPView() {
//        return ResponseEntity.ok(viewDataService.getSTPView());
//    }
//
////    @GetMapping("/transactions-received")
////    public ResponseEntity<TransactionsReceivedView> getTransactionsReceivedView() {
////        return ResponseEntity.ok(viewDataService.getTransactionsReceivedView());
////    }
//
//    @GetMapping("/transactions-received")
//    public ResponseEntity<?> getTransactionsReceivedView() {
//        TransactionsReceivedView view = viewDataService.getTransactionsReceivedView();
//        if (view == null) {
//            return ResponseEntity.status(404).body("No data found for TransactionsReceivedView");
//        }
//        return ResponseEntity.ok(view);  // This will automatically serialize the object to JSON
//    }
//
//    @GetMapping("/transactions-released")
//    public ResponseEntity<TransactionsReleasedView> getTransactionsReleasedView() {
//        return ResponseEntity.ok(viewDataService.getTransactionsReleasedView());
//    }
//
//    @GetMapping("/on-hold")
//    public ResponseEntity<OnHoldView> getOnHoldView() {
//        return ResponseEntity.ok(viewDataService.getOnHoldView());
//    }
//
//    @GetMapping("/approved")
//    public ResponseEntity<ApprovedView> getApprovedView() {
//        return ResponseEntity.ok(viewDataService.getApprovedView());
//    }
//
//    @GetMapping("/autocorrected")
//    public ResponseEntity<AutocorrectedView> getAutocorrectedView() {
//        return ResponseEntity.ok(viewDataService.getAutocorrectedView());
//    }
//
//    @GetMapping("/repair")
//    public ResponseEntity<RepairView> getRepairView() {
//        return ResponseEntity.ok(viewDataService.getRepairView());
//    }
//
//    @GetMapping("/transaction-value")
//    public ResponseEntity<TransactionValueView> getTransactionValueView() {
//        return ResponseEntity.ok(viewDataService.getTransactionValueView());
//    }
//
//    @GetMapping("/dashboard")
//    public ResponseEntity<DashboardResponse> getDashboardData() {
//        DashboardResponse dashboardResponse = viewDataService.getDashboardData();
//
//        return ResponseEntity.ok(dashboardResponse);
//    }
//}
//
