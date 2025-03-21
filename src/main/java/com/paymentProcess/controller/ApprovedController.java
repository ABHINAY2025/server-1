package com.paymentProcess.controller;

import com.paymentProcess.dto.ApprovedStatusResponse;
import com.paymentProcess.service.paymentService.ApprovedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApprovedController {

    private final ApprovedService approvedService;

    public ApprovedController(ApprovedService approvedService) {
        this.approvedService = approvedService;
    }

    @PostMapping("/approve/paymentFile/{id}")
    public ApprovedStatusResponse approvePaymentFile(@PathVariable String id, @RequestBody String updatedXml) throws Exception {
        boolean isApproved = approvedService.approvePaymentFile(id, updatedXml);
        if (isApproved) {
            ApprovedStatusResponse approvedStatusResponse = new ApprovedStatusResponse();
            approvedStatusResponse.setMessage("Payment file approved successfully.");
            return approvedStatusResponse;
        } else {
            ApprovedStatusResponse approvedStatusResponse = new ApprovedStatusResponse();
            approvedStatusResponse.setMessage("Payment file not found or could not be approved.");
            return approvedStatusResponse;
        }
    }
}
