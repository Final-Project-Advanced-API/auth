package org.example.userservice.model.entity;


import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AppUser {
    private String userId;
    private String username;
    private String fullName;
    private String gender;
    private String dob;
    private String email;
    private String profile;
    private String otpCode;
    private String createdAt;
    private String updateAt;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

}
