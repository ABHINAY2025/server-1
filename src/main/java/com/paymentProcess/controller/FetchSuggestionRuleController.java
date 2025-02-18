package com.paymentProcess.controller;

import com.paymentProcess.dto.ruleSuggestion.ApprvdIdsRequest;
import com.paymentProcess.dto.ruleSuggestion.DupIdsRequest;
import com.paymentProcess.service.paymentService.FetchSuggestionRulesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FetchSuggestionRuleController {

    private final FetchSuggestionRulesService fetchSuggestionRulesService;

    public FetchSuggestionRuleController(FetchSuggestionRulesService fetchSuggestionRulesService){

        this.fetchSuggestionRulesService = fetchSuggestionRulesService;
    }

    @GetMapping("/fetchAllSuggestions/{type}")
    public ResponseEntity<?> getFetchRulesMl(@PathVariable String type) {
        Object response = fetchSuggestionRulesService.fetchRulesSuggestions(type);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/update/dupIdRulesStatus")
    public ResponseEntity<String> updateRulesStatus(@RequestBody DupIdsRequest request) {
        try {
            fetchSuggestionRulesService.updateRulesStatusToDuplicates(request);
            return ResponseEntity.ok("Rules status updated to 'Duplicates' successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating rules status.");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<String> processApprovedIds(@RequestBody ApprvdIdsRequest request) {
        try {
            fetchSuggestionRulesService.processApprvdIds(request);
            return ResponseEntity.ok("Rules processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while processing the request.");
        }
    }
}
