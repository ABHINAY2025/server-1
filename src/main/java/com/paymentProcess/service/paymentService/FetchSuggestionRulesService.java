package com.paymentProcess.service.paymentService;


import com.paymentProcess.dto.ruleSuggestion.ApprvdIdsRequest;
import com.paymentProcess.dto.ruleSuggestion.DupIdsRequest;
import com.paymentProcess.dto.ruleSuggestion.RulesRequest;
import com.paymentProcess.entity.ruleSuggestion.MasterCollection;
import com.paymentProcess.entity.ruleSuggestion.StageCollection;
import com.paymentProcess.repository.rulesSuggestion.MasterRepository;
import com.paymentProcess.repository.rulesSuggestion.StageCollectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FetchSuggestionRulesService {

    @Value("${api.Ml.url}")
    private String ml_Api;

    @Autowired
    private StageCollectionRepository stageCollectionRepository;

    @Autowired
    private MasterRepository masterRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final static Logger logger = LoggerFactory.getLogger(FetchSuggestionRulesService.class);

    public ResponseEntity<?> fetchRulesSuggestions(String type) {
        try {
            String url = ml_Api;

            RulesRequest request = new RulesRequest();
            request.setType(type);

            HttpEntity<RulesRequest> httpEntity = new HttpEntity<>(request);

            ResponseEntity<Object> response = restTemplate.exchange(
                    url, HttpMethod.POST, httpEntity, Object.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully fetched rules suggestions.");
                return ResponseEntity.ok(response.getBody());
            } else {
                logger.error("Failed to fetch rules suggestions. Status code: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("Error while fetching rules suggestions", e);
            return null;
        }
    }

    public void updateRulesStatusToDuplicates(DupIdsRequest request) {
        List<String> dupids = request.getDupids();

        List<StageCollection> stageCollections = stageCollectionRepository.findBy_idIn(dupids);

        for (StageCollection stageCollection : stageCollections) {
            stageCollection.setRulesStatus("Duplicate");
            stageCollectionRepository.save(stageCollection);
        }
    }

    public void processApprvdIds(ApprvdIdsRequest request) {
        List<String> apprvdIds = request.getApprvdIds();

        List<StageCollection> stageCollections = stageCollectionRepository.findBy_idIn(apprvdIds);
        for (StageCollection stageCollection : stageCollections) {
            stageCollection.setRulesStatus("Analysed");
            stageCollectionRepository.save(stageCollection);
        }

        for (String apprvdId : apprvdIds) {
            StageCollection updatedStageCollection = stageCollections.stream()
                    .filter(stageCollection -> stageCollection.get_id().equals(apprvdId))
                    .findFirst()
                    .orElse(null);

            if (updatedStageCollection != null) {
                List<StageCollection> duplicateStageCollections = stageCollectionRepository.findByRulesStatus(apprvdId).stream()
                        .filter(stageCollection -> isDuplicate(stageCollection, updatedStageCollection))
                        .collect(Collectors.toList());

                if (!duplicateStageCollections.isEmpty()) {
                    for (StageCollection duplicate : duplicateStageCollections) {
                        duplicate.setRulesStatus("Duplicates");
                        stageCollectionRepository.save(duplicate);
                    }
                } else {
                    createNewRuleForMasterCollection(updatedStageCollection);

                    updatedStageCollection.setRulesStatus("Approved");
                    stageCollectionRepository.save(updatedStageCollection);
                }
            }
        }

    }

    private boolean isDuplicate(StageCollection existing, StageCollection updated) {
        boolean isSameCustomer = existing.getCustomerName().equals(updated.getCustomerName());
        boolean isSameWhen = existing.getWhen().equals(updated.getWhen());
        boolean isSameThen = existing.getThen().equals(updated.getThen());
        return isSameCustomer && isSameWhen && isSameThen;
    }

    private void createNewRuleForMasterCollection(StageCollection stageCollection) {
        MasterCollection newMasterCollection = new MasterCollection();
        newMasterCollection.setWhen(stageCollection.getWhen());
        newMasterCollection.setThen(stageCollection.getThen());
        newMasterCollection.setIsActive(true);
        newMasterCollection.setCustomerName(stageCollection.getCustomerName());
        newMasterCollection.setNetworkName(stageCollection.getNetworkName());
        newMasterCollection.setRuleName(stageCollection.getRuleName());
        newMasterCollection.setRuleType(stageCollection.getRuleType());
        newMasterCollection.setRuleSubType(stageCollection.getRuleSubType());
        newMasterCollection.setRuleDescription(stageCollection.getRuleDescription());
        newMasterCollection.setSelectedStartDate(stageCollection.getSelectedStartDate());
        newMasterCollection.setSelectedEndDate(stageCollection.getSelectedEndDate());

        masterRepository.save(newMasterCollection);
    }

}
