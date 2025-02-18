package com.paymentProcess.controller.viewController;

import com.paymentProcess.service.viewService.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/view")
public class CreateViewsController {

    private final TransactionReceivedViewService transactionReceivedViewService;
    private final AutoCorrectedViewService autoCorrectedViewService;
    private final STPViewService stpViewService;
    private final ApprovedViewService approvedViewService;
    private final OnHoldViewService onHoldViewService;
    private final TransactionValueViewService transactionValueViewService;
    private final RepairViewService repairViewService;
    private final ReleasedViewService releasedViewService;
    private final DashboardViewService dashboardViewService;

    public CreateViewsController(TransactionReceivedViewService transactionReceivedViewService, AutoCorrectedViewService autoCorrectedViewService, STPViewService stpViewService, ApprovedViewService approvedViewService, OnHoldViewService onHoldViewService, TransactionValueViewService transactionValueViewService, RepairViewService repairViewService, ReleasedViewService releasedViewService, DashboardViewService dashboardViewService) {
        this.transactionReceivedViewService = transactionReceivedViewService;
        this.autoCorrectedViewService = autoCorrectedViewService;
        this.stpViewService = stpViewService;
        this.approvedViewService = approvedViewService;
        this.onHoldViewService = onHoldViewService;
        this.transactionValueViewService = transactionValueViewService;
        this.repairViewService = repairViewService;
        this.releasedViewService = releasedViewService;
        this.dashboardViewService = dashboardViewService;
    }


    // Endpoint to trigger the creation of the view
    @PostMapping("/createTransactionReceivedView")
    public String createTransactionReceivedView() {
        try {
            transactionReceivedViewService.createTransactionReceivedView();
            return "TransReceivedView created successfully";
        } catch (Exception e) {
            return "Error creating view: " + e.getMessage();
        }
    }

    @PostMapping("/createAutoCorrectedView")
    public String createAutoCorrectedView() {
        autoCorrectedViewService.createAutoCorrectedView();
        return "AutoCorrect View Created Successfully!";
    }

    // Endpoint to trigger the creation of the repair view

    @PostMapping("/createStpView")
    public String createStpView() {
        try {
            // Call the service to create the view
            stpViewService.createSTPView();
            return "STP view created successfully";
        } catch (Exception e) {
            return "Error creating STP view: " + e.getMessage();
        }
    }

    @PostMapping("/createApprovedView")
    public String createApprovedView() {
        try {
            // Call the service to create the view
            approvedViewService.createApprovedView();
            return "Approved view created successfully";
        } catch (Exception e) {
            return "Error creating Approved view: " + e.getMessage();
        }
    }

    @PostMapping("/createOnHoldView")
    public String createOnHoldView() {
        try {
            onHoldViewService.createOnHoldView();
            // Call the service to create the view
            return "OnHold view created successfully";
        } catch (Exception e) {
            return "Error creating OnHold view: " + e.getMessage();
        }
    }

    @PostMapping("/createRepairedView")
    public String createRepairedView() {
        try {
            // Call the service to create the view
            repairViewService.createRepairView();
            return "Repaired view created successfully";
        } catch (Exception e) {
            return "Error creating Repaired view: " + e.getMessage();
        }
    }

    @PostMapping("/createTransactionValueView")
    public String createTransactionValueView() {
        try {
            // Call the service to create the view
            transactionValueViewService.createTransactionValueView();
            return "TransactionValue view created successfully";
        } catch (Exception e) {
            return "Error creating TransactionValue view: " + e.getMessage();
        }
    }

    @PostMapping("/createTransactionReleasedView")
    public String createTransactionReleasedView() {
        try {
            // Call the service to create the view
            releasedViewService.createReleasedView();
            return "TransactionReleased view created successfully";
        } catch (Exception e) {
            return "Error creating TransactionReleased view: " + e.getMessage();
        }
    }

    @PostMapping("/createDashboardView")
    public String createDashboardView() {
        try {
            // Call the service to create the view
            dashboardViewService.createDashboardView();
            return "Dashboard view created successfully";
        } catch (Exception e) {
            return "Error creating Dashboard view: " + e.getMessage();
        }
    }


}

