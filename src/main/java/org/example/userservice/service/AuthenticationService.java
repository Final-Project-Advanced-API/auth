package org.example.userservice.service;

import jakarta.mail.MessagingException;
import org.example.userservice.model.dto.request.UserRequest;

import org.example.userservice.model.response.UserResponse;


public interface AuthenticationService {
    UserResponse registerUser(UserRequest userRequest) throws MessagingException;
    void verify(String email, String otpCode, Boolean type);
}
