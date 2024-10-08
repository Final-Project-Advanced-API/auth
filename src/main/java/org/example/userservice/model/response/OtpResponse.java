package org.example.userservice.model.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OtpResponse {
    private String otpCode;
    private boolean verify;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private String userId;

}
