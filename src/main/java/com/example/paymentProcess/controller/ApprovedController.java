package com.example.paymentProcess.controller;

import com.example.paymentProcess.service.paymentService.ApprovedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApprovedController {

    @Autowired
    private ApprovedService paymentFileService;

    @PostMapping("/approve/paymentFile/{id}")
    public String approvePaymentFile(@PathVariable String id, @RequestBody String updatedXml) throws Exception {
        boolean isApproved = paymentFileService.approvePaymentFile(id, updatedXml);
        if (isApproved) {
            return "Payment file approved successfully.";
        } else {
            return "Payment file not found or could not be approved.";
        }
    }
}
