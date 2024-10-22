package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.service.AuthenticationService;
import org.example.userservice.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register with your information")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest userRequest) throws MessagingException {
        UserResponse user = authenticationService.registerUser(userRequest);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Register has been successfully.")
                .code(201)
                .payload(user)
                .status(HttpStatus.CREATED)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify otp")
    public ResponseEntity<?> verify(@RequestParam @Email String email, @RequestParam(defaultValue = "false") Boolean type,@RequestParam String otpCode) {
        authenticationService.verify(email ,otpCode, type);
        APIResponse<?> response = APIResponse.builder()
                .message("Your account is verify successful.")
                .code(200)
                .status(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/resendOtp")
    @Operation(summary = "resent otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email, @RequestParam(defaultValue = "false") Boolean type) throws MessagingException {
        authenticationService.resend(email, type);
        APIResponse<?> response = APIResponse.builder()
               .message("Otp has been sent successfully.")
                .code(200)
               .status(HttpStatus.OK)
               .time(LocalDateTime.now())
               .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/forget")
    @Operation(summary = "Forgot password")
    public ResponseEntity<?> forget(@RequestParam @Email String email, @Valid @RequestBody PasswordRequest passwordRequest) {
        authenticationService.forget(email, passwordRequest);
        APIResponse<?> response = APIResponse.builder()
               .message("Your password is reset successful")
                .code(200)
                .payload(null)
               .status(HttpStatus.OK)
               .time(LocalDateTime.now())
               .build();
        return ResponseEntity.ok(response);
    }

}
