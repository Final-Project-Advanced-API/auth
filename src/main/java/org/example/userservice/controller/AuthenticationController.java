package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
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
//@SecurityRequirement(name = "user")
@RequestMapping("api/v1/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register with your information")
    public ResponseEntity<APIResponse<UserResponse>> register(@RequestBody @Valid UserRequest userRequest) throws MessagingException {
        UserResponse user = authenticationService.registerUser(userRequest);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
                .message("Register has been successfully.")
                .payload(user)
                .status(HttpStatus.CREATED)
                .time(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/verify")
    @Operation(summary = "verify otp")
    public ResponseEntity<APIResponse<UserResponse>> verify(@RequestParam @Positive String otpCode){
        authenticationService.verify(otpCode);
        APIResponse<UserResponse> response = APIResponse.<UserResponse>builder()
               .message("Your account is verify successful.")
               .status(HttpStatus.OK)
               .time(LocalDateTime.now())
               .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/resendOtp")
    @Operation(summary = "resent otp")
    public ResponseEntity <?>resendOtp(@RequestParam String email ) throws MessagingException {
        authenticationService.resendOtp(email);
        APIResponse<?> response = APIResponse.builder()
               .message("Otp has been sent successfully.")
               .status(HttpStatus.OK)
               .time(LocalDateTime.now())
               .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/forget")
    @Operation(summary = "Forgot password")
    public ResponseEntity<?> forget (@RequestParam @Email String email, @Valid @RequestBody PasswordRequest passwordRequest){
        authenticationService.forget(email, passwordRequest);
        APIResponse<?> response = APIResponse.builder()
               .message("Your password is reset successful")
                .payload(null)
               .status(HttpStatus.OK)
               .time(LocalDateTime.now())
               .build();
        return ResponseEntity.ok(response);
    }

}
