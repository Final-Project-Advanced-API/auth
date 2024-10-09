package org.example.userservice.repository;
import org.example.userservice.model.entity.OTP;
import org.example.userservice.model.response.OtpResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<OTP,String> {
    OTP findOTPByOtpCode(String otpCode);
    Optional<OtpResponse> findByUserId(String userId);
}
