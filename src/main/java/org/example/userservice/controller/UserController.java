package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.userservice.model.dto.request.CurrentUserRequest;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UpdatePasswordRequest;
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
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/v1/users")
@SecurityRequirement(name = "stack-notes")
public class UserController {
    private final UserService userService;
    @GetMapping("/user-profile")
    @Operation(summary = "User profile")
    public ResponseEntity<APIResponse<UserResponse>> getCurrentUser(Principal principal) {
        UserResponse user = userService.getCurrentUser(principal.getName());
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Get current user successfully.")
                .code(200)
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
                .code(200)
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
                .code(200)
                .payload(user)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/{password}")
    @Operation(summary = "Reset password")
    ResponseEntity<?> changePassword(Principal principal, @RequestBody @Valid UpdatePasswordRequest updatePasswordRequest) {
        userService.changePassword(principal.getName(),updatePasswordRequest);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Change password current user successfully.")
                .code(200)
                .payload(null)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping
    @Operation(summary = "Update current user")
    ResponseEntity<?> updateUser(Principal principal, @RequestBody @Valid CurrentUserRequest userRequest) {
        UserResponse user = userService.updateCurrentUser(principal.getName(), userRequest);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Update current user successfully.")
                .code(200)
                .payload(user)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
