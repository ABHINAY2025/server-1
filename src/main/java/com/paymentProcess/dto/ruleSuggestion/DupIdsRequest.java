package com.paymentProcess.dto.ruleSuggestion;

import lombok.Data;

import java.util.List;
@Data
public class DupIdsRequest {

    private List<String> dupids;
}
