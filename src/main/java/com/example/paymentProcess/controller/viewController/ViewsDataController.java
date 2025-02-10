package com.example.paymentProcess.controller.viewController;

import com.example.paymentProcess.service.viewService.ViewsDataService;
import com.example.paymentProcess.views_entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/views")
public class ViewsDataController {

    @Autowired
    private ViewsDataService viewsDataService;

    @GetMapping("/stp")
    public ResponseEntity<STPView> getSTPView() {
        return ResponseEntity.ok(viewsDataService.getSTPView());
    }

    @GetMapping("/transactions-received")
    public ResponseEntity<?> getTransactionsReceivedView() {
        TransactionsReceivedView view = viewsDataService.getTransactionsReceivedView();
        if (view == null) {
            return ResponseEntity.status(404).body("No data found for TransactionsReceivedView");
        }
        return ResponseEntity.ok(view);
    }

    @GetMapping("/transactions-released")
    public ResponseEntity<TransactionsReleasedView> getTransactionsReleasedView() {
        return ResponseEntity.ok(viewsDataService.getTransactionsReleasedView());
    }

    @GetMapping("/on-hold")
    public ResponseEntity<OnHoldView> getOnHoldView() {
        return ResponseEntity.ok(viewsDataService.getOnHoldView());
    }

    @GetMapping("/approved")
    public ResponseEntity<ApprovedView> getApprovedView() {
        return ResponseEntity.ok(viewsDataService.getApprovedView());
    }

    @GetMapping("/autocorrected")
    public ResponseEntity<AutocorrectedView> getAutocorrectedView() {
        return ResponseEntity.ok(viewsDataService.getAutocorrectedView());
    }

    @GetMapping("/repair")
    public ResponseEntity<RepairView> getRepairView() {
        return ResponseEntity.ok(viewsDataService.getRepairView());
    }

    @GetMapping("/transaction-value")
    public ResponseEntity<TransactionValueView> getTransactionValueView() {
        return ResponseEntity.ok(viewsDataService.getTransactionValueView());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardData() {
        DashboardResponse dashboardResponse = viewsDataService.getDashboardData();

        return ResponseEntity.ok(dashboardResponse);
    }
}
