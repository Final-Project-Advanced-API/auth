package org.example.userservice.repository;
import org.example.userservice.model.entity.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends MongoRepository<OTP,String> {
    OTP findOTPByOtpCode(String otpCode);

}
