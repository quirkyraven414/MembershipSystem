package com.membership.service;

import com.membership.document.AdditionalCharge;
import com.membership.repository.AdditionalChargeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalChargeService {
    private final AdditionalChargeRepository chargeRepository;

    public AdditionalChargeService(AdditionalChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    public List<ChargeResult> evaluateCharges(String transactionType, String tier, double orderValue) {
        List<AdditionalCharge> activeCharges = chargeRepository.findByActiveTrue();
        List<ChargeResult> applicableCharges = new ArrayList<>();

        for (AdditionalCharge charge : activeCharges) {
            ChargeResult result = evaluateCharge(charge, transactionType, tier, orderValue);
            if (result != null && result.isApplicable()) {
                applicableCharges.add(result);
            }
        }

        return applicableCharges;
    }

    private ChargeResult evaluateCharge(AdditionalCharge charge, String transactionType, String tier, double orderValue) {
        Map<String, Object> rules = charge.getRules();
        
        if (!isApplicable(rules, transactionType, tier, orderValue)) {
            return null;
        }

        double chargeAmount = calculateChargeAmount(rules, orderValue);
        
        return new ChargeResult(
            charge.getChargeName(),
            charge.getChargeType(),
            chargeAmount,
            true,
            buildDescription(charge, rules)
        );
    }

    private boolean isApplicable(Map<String, Object> rules, String transactionType, String tier, double orderValue) {
        if (rules.containsKey("applicableFor")) {
            Object applicableFor = rules.get("applicableFor");
            if (applicableFor instanceof List) {
                List<?> types = (List<?>) applicableFor;
                if (!types.contains(transactionType)) {
                    return false;
                }
            }
        }

        if (rules.containsKey("tiers")) {
            Object tiersObj = rules.get("tiers");
            if (tiersObj instanceof List) {
                List<?> tiers = (List<?>) tiersObj;
                if (!tiers.isEmpty() && !tiers.contains(tier)) {
                    return false;
                }
            }
        }

        if (rules.containsKey("minOrderValue")) {
            double minValue = getDoubleValue(rules.get("minOrderValue"));
            if (orderValue < minValue) {
                return false;
            }
        }

        if (rules.containsKey("maxOrderValue")) {
            double maxValue = getDoubleValue(rules.get("maxOrderValue"));
            if (orderValue > maxValue) {
                return false;
            }
        }

        return true;
    }

    private double calculateChargeAmount(Map<String, Object> rules, double orderValue) {
        String calculationType = (String) rules.getOrDefault("calculationType", "PERCENTAGE");
        double value = getDoubleValue(rules.getOrDefault("value", 0));

        return switch (calculationType) {
            case "PERCENTAGE" -> (orderValue * value) / 100.0;
            case "FIXED" -> value;
            case "TIERED" -> calculateTieredCharge(rules, orderValue);
            default -> 0.0;
        };
    }

    private double calculateTieredCharge(Map<String, Object> rules, double orderValue) {
        if (!rules.containsKey("tiers")) {
            return 0.0;
        }

        Object tiersObj = rules.get("tiers");
        if (!(tiersObj instanceof List)) {
            return 0.0;
        }

        List<?> tiers = (List<?>) tiersObj;
        for (Object tierObj : tiers) {
            if (tierObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tier = (Map<String, Object>) tierObj;
                double min = getDoubleValue(tier.get("min"));
                double max = tier.containsKey("max") ? getDoubleValue(tier.get("max")) : Double.MAX_VALUE;
                
                if (orderValue >= min && orderValue <= max) {
                    String type = tier.containsKey("type") ? (String) tier.get("type") : "PERCENTAGE";
                    double value = getDoubleValue(tier.get("value"));
                    
                    if ("PERCENTAGE".equals(type)) {
                        return (orderValue * value) / 100.0;
                    } else {
                        return value;
                    }
                }
            }
        }
        
        return 0.0;
    }

    private double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private String buildDescription(AdditionalCharge charge, Map<String, Object> rules) {
        String calculationType = (String) rules.getOrDefault("calculationType", "PERCENTAGE");
        double value = getDoubleValue(rules.getOrDefault("value", 0));
        
        return switch (calculationType) {
            case "PERCENTAGE" -> charge.getChargeName() + " (" + value + "%)";
            case "FIXED" -> charge.getChargeName() + " (₹" + value + ")";
            case "TIERED" -> charge.getChargeName() + " (Tiered)";
            default -> charge.getChargeName();
        };
    }

    public AdditionalCharge createCharge(AdditionalCharge charge) {
        return chargeRepository.save(charge);
    }

    public AdditionalCharge updateCharge(String id, AdditionalCharge charge) {
        charge.setId(id);
        return chargeRepository.save(charge);
    }

    public void deleteCharge(String id) {
        chargeRepository.deleteById(id);
    }

    public List<AdditionalCharge> getAllCharges() {
        return chargeRepository.findAll();
    }

    public AdditionalCharge getChargeById(String id) {
        return chargeRepository.findById(id).orElse(null);
    }

    public static class ChargeResult {
        private final String chargeName;
        private final String chargeType;
        private final double amount;
        private final boolean applicable;
        private final String description;

        public ChargeResult(String chargeName, String chargeType, double amount, boolean applicable, String description) {
            this.chargeName = chargeName;
            this.chargeType = chargeType;
            this.amount = amount;
            this.applicable = applicable;
            this.description = description;
        }

        public String getChargeName() { return chargeName; }
        public String getChargeType() { return chargeType; }
        public double getAmount() { return amount; }
        public boolean isApplicable() { return applicable; }
        public String getDescription() { return description; }
    }
}
