package com.example.paymentProcess.service;

import com.example.paymentProcess.entity.PaymentFile;
import com.example.paymentProcess.repository.PaymentFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class PaymentFileServiceImp {

    private final PaymentFileRepository paymentFileRepository;


    public PaymentFileServiceImp(PaymentFileRepository paymentFileRepository) {
        this.paymentFileRepository = paymentFileRepository;

    }

    public List<PaymentFile> getAllPaymentFiles(){
        List<PaymentFile> paymentFiles = paymentFileRepository.findAll();
        Double count = (double) paymentFiles.size();
        log.info("PaymentFiles: {} {}",count,paymentFiles);
        return paymentFiles;
    }


}
