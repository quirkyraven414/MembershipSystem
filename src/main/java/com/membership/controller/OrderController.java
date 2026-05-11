package com.membership.controller;

import com.membership.dto.ErrorResponse;
import com.membership.model.Order;
import com.membership.repository.OrderRepository;
import com.membership.service.TierProgressionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final TierProgressionService tierProgressionService;

    public OrderController(OrderRepository orderRepository, 
                          TierProgressionService tierProgressionService) {
        this.orderRepository = orderRepository;
        this.tierProgressionService = tierProgressionService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderRepository.save(order);
            
            TierProgressionService.TierEvaluationResult evaluation = 
                tierProgressionService.evaluateUserTier(order.getUserId());
            
            return ResponseEntity.ok(new OrderResponse(savedOrder, evaluation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Order Creation Failed", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/user/{userId}/evaluate-tier")
    public ResponseEntity<?> evaluateTier(@PathVariable Long userId) {
        try {
            TierProgressionService.TierEvaluationResult result = 
                tierProgressionService.evaluateUserTier(userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Evaluation Failed", e.getMessage()));
        }
    }

    public static class OrderResponse {
        private final Order order;
        private final TierProgressionService.TierEvaluationResult tierEvaluation;

        public OrderResponse(Order order, TierProgressionService.TierEvaluationResult tierEvaluation) {
            this.order = order;
            this.tierEvaluation = tierEvaluation;
        }

        public Order getOrder() { return order; }
        public TierProgressionService.TierEvaluationResult getTierEvaluation() { return tierEvaluation; }
    }
}
