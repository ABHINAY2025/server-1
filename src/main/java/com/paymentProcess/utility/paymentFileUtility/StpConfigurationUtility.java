package com.paymentProcess.utility.paymentFileUtility;

import com.paymentProcess.entity.Payments;
import com.paymentProcess.entity.StpConfigurations;
import com.paymentProcess.repository.StpConfigurationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StpConfigurationUtility {

    private final StpConfigurationRepository stpConfigurationRepository;

    public StpConfigurationUtility(StpConfigurationRepository stpConfigurationRepository) {
        this.stpConfigurationRepository = stpConfigurationRepository;
    }

    public void applyStpConfigurations(Payments payment) {
        Payments.Creditor creditor = payment.getCreditor();
        Payments.Debtor debtor = payment.getDebtor();
        List<StpConfigurations> activeConfigurations = getStpConfigurationsForCustomer(creditor, debtor);

        Payments.RuleID debtorRuleID = new Payments.RuleID();
        Payments.RuleID creditorRuleID = new Payments.RuleID();

        List<String> debtorCustomerRulesList = new ArrayList<>();
        List<String> creditorCustomerRulesList = new ArrayList<>();

        List<Payments.STPConfig> stpConfigRules = new ArrayList<>();

        boolean debtorRuleApplied = false;
        boolean creditorRuleApplied = false;

        for (StpConfigurations config : activeConfigurations) {
            boolean appliesToDebtor = config.getRuleType().equals("Debit Side") && config.getCustomerName().equals(debtor.getDebtorName());
            boolean appliesToCreditor = config.getRuleType().equals("Credit Side") && config.getCustomerName().equals(creditor.getCreditorName());

            if (appliesToDebtor || appliesToCreditor) {
                for (Object whenConditionObject : config.getWhen()) {
                    if (whenConditionObject instanceof Map) {
                        Map<String, String> whenConditionMap = (Map<String, String>) whenConditionObject;

                        String isoWhenField = whenConditionMap.get("ISOWhenField");
                        String isoWhenOperator = whenConditionMap.get("ISOWhenOperator");
                        String isoWhenValue = whenConditionMap.get("ISOWhenValue");

                        boolean conditionMet = checkWhenCondition(payment, isoWhenField, isoWhenOperator, isoWhenValue);

                        if (conditionMet) {
                            applyThenCondition(payment, config.getThen());

                            if (appliesToDebtor) {
                                debtorCustomerRulesList.add(config.getId());
                                debtorRuleApplied = true;
                            }
                            if (appliesToCreditor) {
                                creditorCustomerRulesList.add(config.getId());
                                creditorRuleApplied = true;
                            }
                        }
                    }
                }
            }
        }

        debtorRuleID.setCustomer_rules(debtorCustomerRulesList);
        debtorRuleID.setType("Debtor");

        creditorRuleID.setCustomer_rules(creditorCustomerRulesList);
        creditorRuleID.setType("Creditor");


        List<Payments.RuleID> ruleIDsList = new ArrayList<>();
        ruleIDsList.add(debtorRuleID);
        ruleIDsList.add(creditorRuleID);

        payment.setRuleIDs(ruleIDsList);

        Payments.STPConfig stpConfig = new Payments.STPConfig();
        stpConfig.setDebtorCustomerRule(debtorRuleApplied);
        stpConfig.setCreditorCustomerRule(creditorRuleApplied);

        stpConfigRules.add(stpConfig);
        payment.setStpConfigRules(stpConfigRules);
    }


    public List<StpConfigurations> getStpConfigurationsForCustomer(Payments.Creditor creditor, Payments.Debtor debtor) {
        List<StpConfigurations> configurations = new ArrayList<>();

        if (creditor != null && debtor != null) {
            List<StpConfigurations> stpConfigurationsList = stpConfigurationRepository.findByCustomerName(creditor.getCreditorName());

            for (StpConfigurations stpConfig : stpConfigurationsList) {
                boolean appliesToCreditor = stpConfig.getCustomerName().equals(creditor.getCreditorName()) && stpConfig.getRuleType().equals("Credit Side");

                boolean appliesToDebtor = stpConfig.getCustomerName().equals(debtor.getDebtorName()) && stpConfig.getRuleType().equals("Debit Side");

                if (appliesToCreditor || appliesToDebtor) {
                    configurations.add(stpConfig);
                }
            }

            List<StpConfigurations> debtorConfigurations = stpConfigurationRepository.findByCustomerName(debtor.getDebtorName());
            for (StpConfigurations stpConfig : debtorConfigurations) {
                boolean appliesToCreditor = stpConfig.getCustomerName().equals(creditor.getCreditorName()) && stpConfig.getRuleType().equals("Credit Side");

                boolean appliesToDebtor = stpConfig.getCustomerName().equals(debtor.getDebtorName()) && stpConfig.getRuleType().equals("Debit Side");

                if (appliesToCreditor || appliesToDebtor) {
                    configurations.add(stpConfig);
                }
            }
        }

        return configurations;
    }

    public boolean checkWhenCondition(Payments payment, String isoWhenField, String isoWhenOperator, String isoWhenValue) {
        String fieldValue = extractFieldValue(payment, isoWhenField);

        switch (isoWhenOperator) {
            case "===":
                return fieldValue.equals(isoWhenValue);
            case ">=":
                Double amount = payment.getAmount();
                if (amount != null) {
                    Double conditionValue = Double.valueOf(isoWhenValue);
                    return amount.compareTo(conditionValue) >= 0;
                }
                break;
        }

        return false;
    }

    public void applyThenCondition(Payments payment, List<Object> thenConditions) {
        for (Object condition : thenConditions) {
            if (condition instanceof Map) {
                Map<String, String> thenMap = (Map<String, String>) condition;
                for (String field : thenMap.keySet()) {
                    String newValue = thenMap.get(field);
                    updateFieldValue(payment, field, newValue);
                }
            }
        }
    }

    public void updateFieldValue(Payments payment, String field, String newValue) {
        switch (field) {
            case "ns0:DbtrAcct/ns0:Id/ns0:Othr/ns0:Id":
                if (payment.getDebtor() != null) {
                    payment.getDebtor().setDebtorIban(newValue);
                }
                break;
            case "ns0:DbtrAgt/ns0:FinInstnId/ns0:BICFI":
                if (payment.getDebtor() != null) {
                    payment.getDebtor().setDebtorBic(newValue);
                }
                break;
            case "ns0:CdtrAcct/ns0:Id/ns0:Othr/ns0:Id":
                if (payment.getCreditor() != null) {
                    payment.getCreditor().setCreditorIban(newValue);
                }
                break;
            case "ns0:CdtrAgt/ns0:FinInstnId/ns0:BICFI":
                if (payment.getCreditor() != null) {
                    payment.getCreditor().setCreditorBic(newValue);
                }
                break;
        }
    }

    public String extractFieldValue(Payments payment, String field) {
        switch (field) {
            case "ns0:DbtrAcct/ns0:Id/ns0:Othr/ns0:Id":
                return payment.getDebtor() != null ? payment.getDebtor().getDebtorIban() : null;
            case "ns0:DbtrAgt/ns0:FinInstnId/ns0:BICFI":
                return payment.getDebtor() != null ? payment.getDebtor().getDebtorBic() : null;
            case "ns0:CdtrAcct/ns0:Id/ns0:Othr/ns0:Id":
                return payment.getCreditor() != null ? payment.getCreditor().getCreditorIban() : null;
            case "ns0:CdtrAgt/ns0:FinInstnId/ns0:BICFI":
                return payment.getCreditor() != null ? payment.getCreditor().getCreditorBic() : null;
            case "ns0:IntrBkSttlmAmt/_":
                return payment.getAmount() != null ? payment.getAmount().toString() : null;
        }
        return null;
    }

}

