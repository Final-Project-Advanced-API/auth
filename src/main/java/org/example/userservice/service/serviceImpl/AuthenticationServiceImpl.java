package org.example.userservice.service.serviceImpl;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.core.Response;
import org.example.userservice.exception.BadRequestException;
import org.example.userservice.exception.ConflictException;
import org.example.userservice.exception.NotFoundException;
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
import java.time.LocalDateTime;
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
        UserRepresentation representation = prepareUserRepresentation(userRequest, preparePasswordRepresentation(userRequest.getPassword()));
        UsersResource userResource = keycloak.realm(realm).users();
        Response response = userResource.create(representation);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new ConflictException("This email is already registered");
        }
        UserRepresentation userRepresentation = userResource.get(CreatedResponseUtil.getCreatedId(response)).toRepresentation();
        emailService.sendMail(userRequest.getEmail() ,userRepresentation.getAttributes().get("otpCode").getFirst());
        UserResponse user = modelMapper.map(userRepresentation, UserResponse.class);
        System.out.println(user);
        user.setGender(userRepresentation.getAttributes().get("gender").getFirst());
        user.setFullName(userRepresentation.getAttributes().get("fullName").getFirst());
        user.setDob(userRepresentation.getAttributes().get("dob").getFirst());
        user.setProfile(userRepresentation.getAttributes().get("profile").getFirst());
        user.setCreatedAt(userRepresentation.getAttributes().get("createdAt").getFirst());
        user.setUpdateAt(userRepresentation.getAttributes().get("updateAt").getFirst());
        return user;
    }
@Override
public void verify(String email, String otpCode, Boolean type) {
    Optional<UserRepresentation> userRepresentationOpt = getUserByEmail(email);
    UsersResource userResource = keycloak.realm(realm).users();

    if (userRepresentationOpt.isPresent()) {
        UserRepresentation userRepresentation = userRepresentationOpt.get();
        if (userRepresentation.isEnabled()) {
            throw new BadRequestException("Your account is already verified");
        }
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
            userRepresentation.setEnabled(true);
        } else {
            userRepresentation.singleAttribute("isForgot", String.valueOf(true));
        }
        userResource.get(user.getUserId()).update(userRepresentation);
    } else {
        throw new BadRequestException("User not found");
    }
}

    private UserRepresentation prepareUserRepresentation(UserRequest userRequest, CredentialRepresentation credentialRepresentation) throws MessagingException {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUserName());
        userRepresentation.singleAttribute("gender", userRequest.getGender());
        userRepresentation.singleAttribute("dob", userRequest.getDob());
        userRepresentation.singleAttribute("fullName", userRequest.getFullName());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.singleAttribute("isForgot", String.valueOf(false));
        userRepresentation.singleAttribute("profile",userRequest.getProfile());
        userRepresentation.singleAttribute("createdAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("updateAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("issuedAt", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("expiredAt", String.valueOf(LocalDateTime.now().plusMinutes(2L)));
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


}
