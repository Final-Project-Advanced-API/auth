package org.example.userservice.model.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UpdateUserRequest {
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
}
