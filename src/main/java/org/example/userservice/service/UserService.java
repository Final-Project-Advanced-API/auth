package org.example.userservice.service;

import org.example.userservice.model.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getCurrentUser(String name);
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(UUID userId);
}
