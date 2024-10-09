package org.example.userservice.service;

import jakarta.mail.MessagingException;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.response.UserResponse;

public interface AuthenticationService {
    UserResponse registerUser(UserRequest userRequest) throws MessagingException;
    void verify(String otpCode);
    void resendOtp(String email) throws MessagingException;
    void forget(String email, PasswordRequest passwordRequest);
}
