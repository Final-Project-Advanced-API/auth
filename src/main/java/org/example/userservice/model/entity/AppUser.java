package org.example.userservice.model.entity;


import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AppUser {
    @Id
    private String userId;
    private String userName;
    private String fullName;
    private String gender;
    private String dob;
    private String email;
    private String password;
    private Builder isVerified;
    private String profile;
    private String createdAt;
    private String updateAt;
}
