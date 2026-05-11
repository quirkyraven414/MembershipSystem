package com.membership.config;

import com.membership.model.PricingAdjustment;
import com.membership.repository.PricingAdjustmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {  
    private final PricingAdjustmentRepository adjustmentRepository;

    public DataInitializer(PricingAdjustmentRepository adjustmentRepository) {
        this.adjustmentRepository = adjustmentRepository;
    }

    @Override
    public void run(String... args) {
        if (adjustmentRepository.count() == 0) {
            adjustmentRepository.save(new PricingAdjustment("UPGRADE_DISCOUNT", "PERCENTAGE", -10, true, 1));
            adjustmentRepository.save(new PricingAdjustment("DOWNGRADE_PENALTY", "PERCENTAGE", 5, true, 2));
        }
    }
}
