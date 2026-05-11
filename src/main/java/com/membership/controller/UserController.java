package com.membership.controller;

import com.membership.dto.ErrorResponse;
import com.membership.dto.GetUserRequest;
import com.membership.model.Membership;
import com.membership.model.User;
import com.membership.repository.MembershipRepository;
import com.membership.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public UserController(UserRepository userRepository, MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("User Already Exists", 
                    "User with email " + user.getEmail() + " already exists"));
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getUsers(@RequestBody GetUserRequest request) {
        List<User> users = new ArrayList<>();
        
        if (request.getUserId() != null) {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                users.add(user.get());
            }
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if (user.isPresent() && !users.contains(user.get())) {
                users.add(user.get());
            }
        }
        
        if (request.getMembershipId() != null) {
            Optional<Membership> membership = membershipRepository.findById(request.getMembershipId());
            if (membership.isPresent()) {
                Optional<User> user = userRepository.findById(membership.get().getUserId());
                if (user.isPresent() && !users.contains(user.get())) {
                    users.add(user.get());
                }
            }
        }
        
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User Not Found", 
                    "No users found matching the provided criteria"));
        }
        
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
