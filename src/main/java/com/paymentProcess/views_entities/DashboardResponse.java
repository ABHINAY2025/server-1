package com.paymentProcess.views_entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DashboardResponse {

    @JsonProperty("stpView")
    private STPView stpView;

    @JsonProperty("transactionsReceivedView")
    private TransactionsReceivedView transactionsReceivedView;

    @JsonProperty("transactionsReleasedView")
    private TransactionsReleasedView transactionsReleasedView;

    @JsonProperty("onHoldView")
    private OnHoldView onHoldView;

    @JsonProperty("approvedView")
    private ApprovedView approvedView;

    @JsonProperty("autocorrectedView")
    private AutocorrectedView autocorrectedView;

    @JsonProperty("repairView")
    private RepairView repairView;

    @JsonProperty("transactionValueView")
    private TransactionValueView transactionValueView;

}
