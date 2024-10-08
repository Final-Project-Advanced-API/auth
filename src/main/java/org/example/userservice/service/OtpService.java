package org.example.userservice.service;

import org.example.userservice.model.response.OtpResponse;

public interface OtpService {

    void saveOtp(OtpResponse otpDTO);

    OtpResponse getOtp(String otp);
    void updateOtp(OtpResponse otpResponse);


}
