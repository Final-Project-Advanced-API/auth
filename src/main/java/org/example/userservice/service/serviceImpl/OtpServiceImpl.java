package org.example.userservice.service.serviceImpl;

import org.example.userservice.exception.NotFoundException;
import org.example.userservice.model.entity.OTP;
import org.example.userservice.model.response.OtpResponse;
import org.example.userservice.repository.OtpRepository;
import org.example.userservice.service.OtpService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {
    private final MongoTemplate mongoTemplate;
    private final OtpRepository otpRepository;
    public OtpServiceImpl(MongoTemplate mongoTemplate, OtpRepository otpRepository) {
        this.mongoTemplate = mongoTemplate;
        this.otpRepository = otpRepository;
    }
    @Override
    public void saveOtp(OtpResponse otpResponse) {
        OTP otp =OTP.builder()
                .otpCode(otpResponse.getOtpCode())
                .issuedAt(otpResponse.getIssuedAt())
                .expiredAt(otpResponse.getExpiredAt())
                .userId(otpResponse.getUserId())
                .build();
        otpRepository.save(otp);
    }

    @Override
    public OtpResponse getOtp(String otpCode) {
        OTP otp = otpRepository.findOTPByOtpCode(otpCode);
        if (otp==null){
            throw  new NotFoundException("Otp code not found");
        }
        return OtpResponse.builder()
                .expiredAt(LocalDateTime.from(otp.getExpiredAt()))
                .issuedAt(LocalDateTime.from(otp.getIssuedAt()))
                .otpCode(otp.getOtpCode())
                .userId(otp.getUserId())
                .build();
    }
    @Override
    public void updateOtp(OtpResponse otpResponse) {
        Query query = new Query(Criteria.where("otpCode").is(otpResponse.getOtpCode()));
        Update update = new Update();
        update.set("verify", otpResponse.isVerify());
        mongoTemplate.updateFirst(query, update, OTP.class);
    }

}
