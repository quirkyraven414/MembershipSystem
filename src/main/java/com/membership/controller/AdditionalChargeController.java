package com.membership.controller;

import com.membership.document.AdditionalCharge;
import com.membership.dto.ErrorResponse;
import com.membership.service.AdditionalChargeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charges")
public class AdditionalChargeController {
    private final AdditionalChargeService chargeService;

    public AdditionalChargeController(AdditionalChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @GetMapping
    public ResponseEntity<List<AdditionalCharge>> getAllCharges() {
        return ResponseEntity.ok(chargeService.getAllCharges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChargeById(@PathVariable String id) {
        AdditionalCharge charge = chargeService.getChargeById(id);
        if (charge == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Found", "Charge not found with ID: " + id));
        }
        return ResponseEntity.ok(charge);
    }

    @PostMapping
    public ResponseEntity<?> createCharge(@RequestBody AdditionalCharge charge) {
        try {
            AdditionalCharge created = chargeService.createCharge(charge);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Creation Failed", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCharge(@PathVariable String id, @RequestBody AdditionalCharge charge) {
        try {
            AdditionalCharge existing = chargeService.getChargeById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Charge not found with ID: " + id));
            }
            AdditionalCharge updated = chargeService.updateCharge(id, charge);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Update Failed", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharge(@PathVariable String id) {
        try {
            AdditionalCharge existing = chargeService.getChargeById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Charge not found with ID: " + id));
            }
            chargeService.deleteCharge(id);
            return ResponseEntity.ok(new ErrorResponse("Success", "Charge deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Deletion Failed", e.getMessage()));
        }
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateCharges(@RequestParam String transactionType,
                                             @RequestParam String tier,
                                             @RequestParam double orderValue) {
        try {
            List<AdditionalChargeService.ChargeResult> results = 
                chargeService.evaluateCharges(transactionType, tier, orderValue);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Evaluation Failed", e.getMessage()));
        }
    }
}
