package com.example.paymentProcess.service.viewService;

import com.example.paymentProcess.repository.views_views.*;
import com.example.paymentProcess.views_entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
@Service
public class ViewsDataService {

    private final STPViewRepo stpViewRepo;

    private final TransactionsReceivedViewRepo transactionsReceivedViewRepo;

    private final TransactionsReleasedViewRepo transactionsReleasedViewRepo;

    private final OnHoldViewRepo onHoldViewRepo;

    private final ApprovedViewRepo approvedViewRepo;

    private final AutoCorrectedViewRepo autocorrectedViewRepo;

    private final RepairViewRepo repairViewRepo;

    private final TransactionValueViewRepo transactionValueViewRepo;

    private static final Logger logger = LoggerFactory.getLogger(ViewsDataService.class);

    public ViewsDataService(STPViewRepo stpViewRepo, TransactionsReceivedViewRepo transactionsReceivedViewRepo, TransactionsReleasedViewRepo transactionsReleasedViewRepo, OnHoldViewRepo onHoldViewRepo, ApprovedViewRepo approvedViewRepo, AutoCorrectedViewRepo autocorrectedViewRepo, RepairViewRepo repairViewRepo, TransactionValueViewRepo transactionValueViewRepo) {
        this.stpViewRepo = stpViewRepo;
        this.transactionsReceivedViewRepo = transactionsReceivedViewRepo;
        this.transactionsReleasedViewRepo = transactionsReleasedViewRepo;
        this.onHoldViewRepo = onHoldViewRepo;
        this.approvedViewRepo = approvedViewRepo;
        this.autocorrectedViewRepo = autocorrectedViewRepo;
        this.repairViewRepo = repairViewRepo;
        this.transactionValueViewRepo = transactionValueViewRepo;
    }

    public STPView getSTPView() {
        return stpViewRepo.findAll().get(0);
    }

    public TransactionsReceivedView getTransactionsReceivedView() {
        TransactionsReceivedView view = transactionsReceivedViewRepo.findAll().stream().findFirst().orElse(null);
        logger.info("Fetched TransactionsReceivedView: {}", view);
        return view;
    }

    public TransactionsReleasedView getTransactionsReleasedView() {
        TransactionsReleasedView transactionsReleasedView = transactionsReleasedViewRepo.findAll().get(0);
        System.out.println(transactionsReleasedView);
        return transactionsReleasedViewRepo.findAll().get(0);
    }

    public OnHoldView getOnHoldView() {
        OnHoldView onHoldView = onHoldViewRepo.findAll().get(0);
        System.out.println(onHoldView);
        return onHoldViewRepo.findAll().get(0);
    }

    public ApprovedView getApprovedView() {
        ApprovedView approvedView = approvedViewRepo.findAll().get(0);
        System.out.println(approvedView);
        return approvedViewRepo.findAll().get(0);
    }

    public AutocorrectedView getAutocorrectedView() {
        AutocorrectedView autocorrectedView = autocorrectedViewRepo.findAll().get(0);
        System.out.println(autocorrectedView);
        return autocorrectedViewRepo.findAll().get(0);
    }

    public RepairView getRepairView() {
        List<RepairView> repairViews = repairViewRepo.findAll();
        System.out.println("Number of RepairView records found: " + repairViews.size());
        if (repairViews.isEmpty()) {
            throw new NoSuchElementException("No RepairView found.");
        }
        return repairViews.get(0);
    }


    public TransactionValueView getTransactionValueView() {
        TransactionValueView transactionValueView = transactionValueViewRepo.findAll().get(0);
        System.out.println(transactionValueView);
        return transactionValueViewRepo.findAll().get(0);
    }

    public DashboardResponse getDashboardData() {
        DashboardResponse dashboardResponse = new DashboardResponse();

        // Fetch all data from repositories and populate the DTO
        dashboardResponse.setStpView(stpViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setTransactionsReceivedView(transactionsReceivedViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setTransactionsReleasedView(transactionsReleasedViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setOnHoldView(onHoldViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setApprovedView(approvedViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setAutocorrectedView(autocorrectedViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setRepairView(repairViewRepo.findAll().stream().findFirst().orElse(null));
        dashboardResponse.setTransactionValueView(transactionValueViewRepo.findAll().stream().findFirst().orElse(null));

        return dashboardResponse;
    }
}
