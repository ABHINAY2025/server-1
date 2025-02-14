package com.example.paymentProcess.controller;

import com.example.paymentProcess.dto.SwitchNetworkRequest;
import com.example.paymentProcess.entity.Networks;
import com.example.paymentProcess.service.paymentService.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @PostMapping("/networkStatus")
    public ResponseEntity<String> updateNetworkStatus(@RequestBody String xmlPayload) {
        try {
            // Call service to process the XML payload
            networkService.readNetworkMessage(xmlPayload);
            return ResponseEntity.ok("Network status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing network update: " + e.getMessage());
        }
    }

    // Endpoint to update the approval status of a secondary network
    @PutMapping("/switchNetwork")
    public ResponseEntity<?> updateApprovedStatus(@RequestBody SwitchNetworkRequest switchNetworkRequest) {
        try {
            // Get the network and the new approval status from the request body
            Networks updatedNetwork = networkService.updateApprovedStatus(
                    switchNetworkRequest.getSecondaryNetwork().get_id(),
                    switchNetworkRequest.getIsApproved()
            );

            if (updatedNetwork == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Network not found or update failed.");
            }

            // Create a new SwitchNetworkRequest object and set its properties
            SwitchNetworkRequest response = new SwitchNetworkRequest();
            response.setSecondaryNetwork(updatedNetwork);
            response.setIsApproved(switchNetworkRequest.getIsApproved());

            // Return the updated network and approval status
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Handle errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
