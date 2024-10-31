package org.example.userservice.service.serviceImpl;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.core.Response;
import org.example.userservice.exception.BadRequestException;
import org.example.userservice.exception.ConflictException;
import org.example.userservice.exception.NotFoundException;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.entity.AppUser;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.service.AuthenticationService;
import org.example.userservice.service.EmailService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Keycloak keycloak;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Value("${keycloak.realm}")
    private String realm;

    public AuthenticationServiceImpl(Keycloak keycloak, EmailService emailService, ModelMapper modelMapper) {
        this.keycloak = keycloak;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }
@Override
public UserResponse registerUser(UserRequest userRequest) throws MessagingException {
    if (!userRequest.getConfirmPassword().equals(userRequest.getPassword())) {
        throw new BadRequestException("Your confirm password does not match with your password");
    }
    LocalDate dob;
    try {
        dob = LocalDate.parse(userRequest.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (dob.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date of birth cannot be in the future");
        }
    } catch (DateTimeParseException e) {
        throw new BadRequestException("Invalid date format for date of birth, expected format is yyyy-MM-dd");
    }
    String username = extractUsernameFromEmail(userRequest.getEmail());
    UserRepresentation representation = prepareUserRepresentation(userRequest, username, preparePasswordRepresentation(userRequest.getPassword()));
    UsersResource userResource = keycloak.realm(realm).users();
    Response response = userResource.create(representation);

    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        throw new ConflictException("This email is already registered");
    }
    UserRepresentation userRepresentation = userResource.get(CreatedResponseUtil.getCreatedId(response)).toRepresentation();
    emailService.sendMail(userRequest.getEmail(), userRepresentation.getAttributes().get("otpCode").getFirst());

    UserResponse user = modelMapper.map(userRepresentation, UserResponse.class);
    user.setGender(userRepresentation.getAttributes().get("gender").getFirst());
    user.setFullName(userRepresentation.getAttributes().get("fullName").getFirst());
    user.setDob(userRepresentation.getAttributes().get("dob").getFirst());
    user.setBio(null);
    user.setProfile(null);
    user.setCreatedDate(userRepresentation.getAttributes().get("createdDate").getFirst());
    user.setUpdatedDate(userRepresentation.getAttributes().get("updatedDate").getFirst());
    return user;
}
    @Override
    public void verify(String email, String otpCode, Boolean type) {
        Optional<UserRepresentation> userRepresentationOpt = getUserByEmail(email);
        UsersResource userResource = keycloak.realm(realm).users();
        if (userRepresentationOpt.isPresent()) {
            UserRepresentation userRepresentation = userRepresentationOpt.get();
            String storedOtpCode = userRepresentation.firstAttribute("otpCode");
            if (storedOtpCode == null || !storedOtpCode.equals(otpCode)) {
                throw new BadRequestException("Invalid OTP");
            }
            String expirationString = userRepresentation.firstAttribute("expiredAt");
            if (expirationString == null) {
                throw new BadRequestException("Expiration time is missing");
            }
            LocalDateTime expiration = LocalDateTime.parse(expirationString);
            if (!expiration.isAfter(LocalDateTime.now())) {
                throw new BadRequestException("Your OTP has expired");
            }
            AppUser user = modelMapper.map(userRepresentation, AppUser.class);
            if (!type) {
                if (userRepresentation.isEnabled()) {
                    throw new BadRequestException("Your account is already verified");
                }
                userRepresentation.setEnabled(true);
            } else {
                userRepresentation.singleAttribute("isForgot", String.valueOf(true));
            }
            userResource.get(user.getUserId()).update(userRepresentation);
        } else {
            throw new BadRequestException("User not found");
        }
    }
    @Override
    public void resend(String email, Boolean type) throws MessagingException {
        Optional<UserRepresentation> userRepresentationOpt = getUserByEmail(email);
        UsersResource userResource = keycloak.realm(realm).users();
        if (userRepresentationOpt.isPresent()) {
            UserRepresentation userRepresentation = userRepresentationOpt.get();
            LocalDateTime expiration = LocalDateTime.parse(userRepresentation.firstAttribute("expiredAt"));
            if (!expiration.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Your OTP has not expired yet");
            }
            String newOtp = generateOTP();
            userRepresentation.singleAttribute("otpCode", newOtp);
            userRepresentation.singleAttribute("issuedAt", String.valueOf(LocalDateTime.now()));
            userRepresentation.singleAttribute("expiredAt", String.valueOf(LocalDateTime.now().plusMinutes(1L)));
            if (!type) {
                userRepresentation.setEnabled(false);
            } else {
                userRepresentation.singleAttribute("isForgot", String.valueOf(false));
            }
            AppUser user = modelMapper.map(userRepresentation, AppUser.class);
            userResource.get(user.getUserId()).update(userRepresentation);
            emailService.sendMail(email, newOtp);
        } else {
            throw new BadRequestException("User not found");
        }
}

    @Override
    public void forget(String email, PasswordRequest passwordRequest) {
        Optional<UserRepresentation> userRepresentation = getUserByEmail(email);
        if (!passwordRequest.getConfirmPassword().equals(passwordRequest.getPassword())) {
            throw new BadRequestException("Your confirm password does not match with your password");
        }
        if (userRepresentation.isPresent()) {
            boolean isForgot = Boolean.parseBoolean(userRepresentation.get().firstAttribute("isForgot"));
            if (!isForgot) {
                throw new BadRequestException("Please verify your account before forgetting your password");
            }
            UsersResource userResource = keycloak.realm(realm).users();
            AppUser user = modelMapper.map(userRepresentation, AppUser.class);
            userRepresentation.ifPresent(representation -> {
                userRepresentation.get().singleAttribute("isForgot", String.valueOf(false));
                representation.setCredentials(Collections.singletonList(preparePasswordRepresentation(passwordRequest.getPassword())));
                userResource.get(user.getUserId()).update(representation);
            });
        }
    }

    private UserRepresentation prepareUserRepresentation(UserRequest userRequest, String username, CredentialRepresentation credentialRepresentation) throws MessagingException {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.singleAttribute("gender", userRequest.getGender());
        userRepresentation.singleAttribute("dob", userRequest.getDob());
        userRepresentation.singleAttribute("fullName", userRequest.getFullName());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.singleAttribute("isForgot", String.valueOf(false));
        userRepresentation.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("updatedDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("issuedAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("expiredAt", String.valueOf(LocalDateTime.now().plusMinutes(1L)));
        String otp = generateOTP();
        userRepresentation.singleAttribute("otpCode", otp);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setEnabled(false);
        return userRepresentation;
    }
    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    private Optional<UserRepresentation> getUserByEmail(String email) {
        return Optional.ofNullable(keycloak.realm(realm).users()
                .searchByEmail(email, true)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Your email is invalid")));
    }
    private String extractUsernameFromEmail(String email) {
        // Assuming username is the part before the '@'
        return email.substring(0, email.indexOf('@'));
    }


}
