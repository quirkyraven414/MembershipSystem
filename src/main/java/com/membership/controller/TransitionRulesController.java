package com.membership.controller;

import com.membership.service.MembershipTransitionService;
import com.membership.transition.TransitionRule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class TransitionRulesController {
    private final MembershipTransitionService transitionService;

    public TransitionRulesController(MembershipTransitionService transitionService) {
        this.transitionService = transitionService;
    }

    @GetMapping
    public ResponseEntity<List<TransitionRule>> getAllRules() {
        return ResponseEntity.ok(transitionService.getAllRules());
    }
}
