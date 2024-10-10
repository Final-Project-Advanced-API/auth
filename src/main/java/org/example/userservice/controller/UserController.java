package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.service.UserService;
import org.example.userservice.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/users")
@SecurityRequirement(name = "stack-note")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "User profile")
    public ResponseEntity<APIResponse<UserResponse>> getCurrentUser(Principal principal) {
        UserResponse user = userService.getCurrentUser(principal.getName());
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Get current user successfully.")
                .payload(user)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/email")
    @Operation(summary = "Get user by email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Get user by id successfully.")
                .payload(user)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        UserResponse user = userService.getUserById(userId);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Get user by id successfully.")
                .payload(user)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
