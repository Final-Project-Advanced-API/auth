package org.example.userservice.service;

import org.example.userservice.model.response.OtpResponse;

import java.util.Optional;

public interface OtpService {

    void saveOtp(OtpResponse otpDTO);
    OtpResponse getOtp(String otp);
    void updateOtp(OtpResponse otpResponse);
    Optional<OtpResponse> findByUserId(String userId);
    void updateOtpCode(OtpResponse otpResponse);




}
