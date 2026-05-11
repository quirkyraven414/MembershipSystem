package com.membership.controller;

import com.membership.dto.ErrorResponse;
import com.membership.model.TierProgressionRule;
import com.membership.service.TierProgressionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tier-rules")
public class TierRuleController {
    private final TierProgressionService tierProgressionService;

    public TierRuleController(TierProgressionService tierProgressionService) {
        this.tierProgressionService = tierProgressionService;
    }

    @GetMapping
    public ResponseEntity<List<TierProgressionRule>> getAllRules() {
        return ResponseEntity.ok(tierProgressionService.getAllRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRuleById(@PathVariable Long id) {
        TierProgressionRule rule = tierProgressionService.getRuleById(id);
        if (rule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Found", "Rule not found with ID: " + id));
        }
        return ResponseEntity.ok(rule);
    }

    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody TierProgressionRule rule) {
        try {
            TierProgressionRule created = tierProgressionService.createRule(rule);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Creation Failed", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody TierProgressionRule rule) {
        try {
            TierProgressionRule existing = tierProgressionService.getRuleById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Rule not found with ID: " + id));
            }
            TierProgressionRule updated = tierProgressionService.updateRule(id, rule);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Update Failed", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id) {
        try {
            TierProgressionRule existing = tierProgressionService.getRuleById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Rule not found with ID: " + id));
            }
            tierProgressionService.deleteRule(id);
            return ResponseEntity.ok(new ErrorResponse("Success", "Rule deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Deletion Failed", e.getMessage()));
        }
    }
}
