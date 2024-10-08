package org.example.userservice.service.serviceImpl;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.core.Response;
import org.example.userservice.exception.ConflictException;
import org.example.userservice.exception.NotFoundException;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.response.OtpResponse;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.AuthenticationService;
import org.example.userservice.service.EmailService;
import org.example.userservice.service.OtpService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    @Value("${keycloak.realm}")
    private String realm;

    public AuthenticationServiceImpl(Keycloak keycloak, UserRepository userRepository, EmailService emailService, OtpService otpService) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) throws MessagingException {
        UserRepresentation representation = prepareUserRepresentation(userRequest, preparePasswordRepresentation(userRequest.getPassword()));
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(representation);

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new ConflictException("This email is already registered");
        }

        String otpCode = generateOTP();
        emailService.sendMail(userRequest.getEmail(), otpCode);

        String userId = CreatedResponseUtil.getCreatedId(response);

        OtpResponse otpResponse = new OtpResponse();
        otpResponse.setOtpCode(otpCode);
        otpResponse.setUserId(userId);
        otpResponse.setIssuedAt(LocalDateTime.now());
        otpResponse.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // Set expiration time to 5 minutes
        otpService.saveOtp(otpResponse);

        UserResource userResource = usersResource.get(userId);
        UserRepresentation createdUser = userResource.toRepresentation();
        return prepareUserResponse(createdUser);
    }

    @Override
    public String verify(String otpCode) {
        OtpResponse otp = otpService.getOtp(otpCode);
        if (otp.isVerify()) {
            throw new NotFoundException("OTP has already been used");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(otp.getExpiredAt())) {
            throw new NotFoundException("OTP is expired");
        }
        otp.setVerify(true);
        otpService.updateOtp(otp);
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(otp.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.singleAttribute("isVerified", String.valueOf(true));
        userResource.update(userRepresentation);
        return "OTP is verified and user status updated in Keycloak";
    }

    private UserRepresentation prepareUserRepresentation(UserRequest userRequest, CredentialRepresentation credentialRepresentation) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.singleAttribute("fullName", userRequest.getFullName());
        userRepresentation.singleAttribute("gender", userRequest.getGender());
        userRepresentation.singleAttribute("dob", userRequest.getDob());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.singleAttribute("isVerified",String.valueOf(false));
        userRepresentation.singleAttribute("profile",userRequest.getProfile());
        userRepresentation.singleAttribute("createdAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("updateAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }
    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }
    private UserResponse prepareUserResponse(UserRepresentation userRepresentation) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(String.valueOf(UUID.fromString(userRepresentation.getId())));
        userResponse.setUserName(userRepresentation.getUsername());
        userResponse.setCreatedAt(userRepresentation.getAttributes().get("createdAt").getFirst());
        userResponse.setUpdateAt(userRepresentation.getAttributes().get("updateAt").getFirst());
        userResponse.setFullName(userRepresentation.getAttributes().get("fullName").getFirst());
        userResponse.setDob(userRepresentation.getAttributes().get("dob").getFirst());
        userResponse.setGender(userRepresentation.getAttributes().get("gender").getFirst());
        userResponse.setProfile(userRepresentation.getAttributes().get("profile").getFirst());
        userResponse.setIsVerified(Boolean.parseBoolean(userRepresentation.getAttributes().get("isVerified").getFirst()));
        userResponse.setEmail(userRepresentation.getEmail());
        return userResponse;
    }
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generate a 6-digit OTP
        return String.valueOf(otp);
    }
}
