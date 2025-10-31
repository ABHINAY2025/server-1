package com.paymentProcess.controller;

import com.paymentProcess.dto.SwitchNetworkRequest;
import com.paymentProcess.entity.Networks;
import com.paymentProcess.service.paymentService.NetworkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NetworkController {

    private final NetworkService networkService;

    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @PostMapping("/networkStatus")
    public ResponseEntity<String> updateNetworkStatus(@RequestBody String xmlPayload) {
        try {
            networkService.readNetworkMessage(xmlPayload);
            return ResponseEntity.ok("Network status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing network update: " + e.getMessage());
        }
    }

    @PutMapping("/switchNetwork")
    public ResponseEntity<?> updateApprovedStatus(@RequestBody SwitchNetworkRequest switchNetworkRequest) {
        try {
            Networks updatedNetwork = networkService.updateApprovedStatus(
                    switchNetworkRequest.getSecondaryNetwork().getId(),
                    switchNetworkRequest.getIsApproved()
            );

            if (updatedNetwork == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Network not found or update failed.");
            }

            SwitchNetworkRequest response = new SwitchNetworkRequest();
            response.setSecondaryNetwork(updatedNetwork);
            response.setIsApproved(switchNetworkRequest.getIsApproved());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
