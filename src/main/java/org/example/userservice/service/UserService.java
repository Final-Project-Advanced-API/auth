package org.example.userservice.service;

import org.apache.catalina.User;
import org.example.userservice.model.dto.request.CurrentUserRequest;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UpdatePasswordRequest;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getCurrentUser(String name);
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(UUID userId);
    void changePassword(String id, UpdatePasswordRequest updatePasswordRequest);
    UserResponse updateCurrentUser(String id , CurrentUserRequest userRequest);
}
