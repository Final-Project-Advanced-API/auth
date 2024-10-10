package org.example.userservice.service.serviceImpl;

import org.example.userservice.exception.BadRequestException;
import org.example.userservice.exception.NotFoundException;
import org.example.userservice.model.dto.request.CurrentUserRequest;
import org.example.userservice.model.dto.request.PasswordRequest;
import org.example.userservice.model.dto.request.UserRequest;
import org.example.userservice.model.response.UserResponse;
import org.example.userservice.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        if (userRepresentation == null){
            throw new NotFoundException("User not found");
        }
        return getUser(userRepresentation);

    }

    @Override
    public void changePassword(String id, PasswordRequest passwordRequest) {
        if (!passwordRequest.getConfirmPassword().equals(passwordRequest.getPassword())){
            throw new BadRequestException("Your confirmPassword does not match with your password");
        }
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(passwordRequest.getPassword());
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(id).resetPassword(credentialRepresentation);
    }

    @Override
    public UserResponse updateCurrentUser(String id, CurrentUserRequest userRequest) {
         UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
         userRepresentation.setUsername(userRequest.getUserName());
         userRepresentation.setEmail(userRequest.getEmail());
         userRepresentation.singleAttribute("fullName",userRequest.getFullName());
         userRepresentation.singleAttribute("dob", userRequest.getDob());
         userRepresentation.singleAttribute("gender",userRequest.getGender());
         userRepresentation.singleAttribute("profile",userRequest.getProfile());
         UsersResource usersResource = keycloak.realm(realm).users();
         usersResource.get(id).update(userRepresentation);
         return getUser(userRepresentation);

    }

    private UserResponse getUser(UserRepresentation userRepresentation) {
        UserResponse user = modelMapper.map(userRepresentation, UserResponse.class);
        user.setGender(userRepresentation.getAttributes().get("gender").getFirst());
        user.setFullName(userRepresentation.getAttributes().get("fullName").getFirst());
        user.setDob(userRepresentation.getAttributes().get("dob").getFirst());
        user.setProfile(userRepresentation.getAttributes().get("profile").getFirst());
        user.setCreatedAt(userRepresentation.getAttributes().get("createdAt").getFirst());
        user.setUpdateAt(userRepresentation.getAttributes().get("updateAt").getFirst());
        return user;
    }
}
