package org.example.userservice.model.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
@Document(collection="otps")

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OTP {
    @Id
    private String otpId;
    private String otpCode;
    private boolean verify;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private String userId;
}
