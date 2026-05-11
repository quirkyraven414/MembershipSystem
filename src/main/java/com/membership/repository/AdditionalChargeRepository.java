package com.membership.repository;

import com.membership.document.AdditionalCharge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalChargeRepository extends MongoRepository<AdditionalCharge, String> {
    List<AdditionalCharge> findByActiveTrue();
    Optional<AdditionalCharge> findByChargeName(String chargeName);
    List<AdditionalCharge> findByChargeType(String chargeType);
}
