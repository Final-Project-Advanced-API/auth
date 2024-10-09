package org.example.userservice.model.response;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class UserResponse {
    private String userId;
    private String username;
    private String fullName;
    private String gender;
    private String dob;
    private String email;
    private String createdAt;
    private String updateAt;
    private String profile;



}
