package org.example.userservice.service.serviceImpl;

import org.example.userservice.exception.BadRequestException;
import org.example.userservice.exception.NotFoundException;
import org.example.userservice.model.dto.request.CurrentUserRequest;
import org.example.userservice.model.dto.request.UpdatePasswordRequest;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final Keycloak keycloak;
    private final ModelMapper modelMapper;

    public UserServiceImpl(Keycloak keycloak, ModelMapper modelMapper) {
        this.keycloak = keycloak;
        this.modelMapper = modelMapper;
    }

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public UserResponse getCurrentUser(String id) {
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
        return getUser(userRepresentation);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        Optional<UserRepresentation> userRepresentation = keycloak.realm(realm).users().searchByEmail(email, true).stream().findFirst();
        if (userRepresentation.isPresent()) {
            return getUser(userRepresentation.get());
        } else {
            throw new NotFoundException("User not found");
        }
    }
    @Override
    public UserResponse getUserById(UUID userId) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserRepresentation userRepresentation = usersResource.get(String.valueOf(userId)).toRepresentation();
        if (userRepresentation == null) {
            throw new NotFoundException("User not found");
        }
        return getUser(userRepresentation);
    }

    @Override
    public void changePassword(String id, UpdatePasswordRequest updatePasswordRequest) {
        boolean isCurrentPasswordCorrect = validateCurrentPassword(id, updatePasswordRequest.getCurrentPassword());
        if (!isCurrentPasswordCorrect) {
            throw new BadRequestException("Your current password is incorrect");
        }
        if (!updatePasswordRequest.getConfirmPassword().equals(updatePasswordRequest.getNewPassword())) {
            throw new BadRequestException("Your confirmPassword does not match with your password");
        }
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(updatePasswordRequest.getNewPassword());
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(id).resetPassword(credentialRepresentation);
    }

    @Override
    public UserResponse updateCurrentUser(String id, CurrentUserRequest userRequest) {
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
        userRepresentation.singleAttribute("fullName", userRequest.getFullName());
        userRepresentation.singleAttribute("dob", userRequest.getDob());
        userRepresentation.singleAttribute("gender", userRequest.getGender());
        userRepresentation.singleAttribute("profile", userRequest.getProfile());
        userRepresentation.singleAttribute("bio", userRequest.getBio());
        UsersResource usersResource = keycloak.realm(realm).users();
        usersResource.get(id).update(userRepresentation);
        return getUser(userRepresentation);
    }

    private UserResponse getUser(UserRepresentation userRepresentation) {
        UserResponse user = modelMapper.map(userRepresentation, UserResponse.class);

        // Check and set optional user attributes with null safety
        user.setGender(getFirstAttribute(userRepresentation, "gender"));
        user.setFullName(getFirstAttribute(userRepresentation, "fullName"));
        user.setDob(getFirstAttribute(userRepresentation, "dob"));
        user.setBio(getFirstAttribute(userRepresentation, "bio"));
        user.setProfile(getFirstAttribute(userRepresentation, "profile"));
        user.setCreatedDate(getFirstAttribute(userRepresentation, "createdDate"));
        user.setUpdatedDate(getFirstAttribute(userRepresentation, "updatedDate"));

        return user;
    }

    private boolean validateCurrentPassword(String userId, String currentPassword) {
        String clientId = "stack-notes-client";
        String clientSecret = "tDbzKXKsHvvmFQAJ1bUSR87Dbia2ssfZ";
        String realmUrl = "https://keycloak.jelay.site/realms/stack-notes/protocol/openid-connect/token";
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(userId).toRepresentation();
        String username = userRepresentation.getUsername();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", currentPassword);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(realmUrl, HttpMethod.POST, request, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    private String getFirstAttribute(UserRepresentation userRepresentation, String attributeName) {
        return userRepresentation.getAttributes() != null && userRepresentation.getAttributes().get(attributeName) != null
                ? userRepresentation.getAttributes().get(attributeName).isEmpty() ? null : userRepresentation.getAttributes().get(attributeName).getFirst()
                : null;
    }

}
