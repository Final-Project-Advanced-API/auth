package org.example.userservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CurrentUserRequest {
    @NotNull
    @NotBlank
    private String fullName;

    @NotNull
    @NotBlank
    private String gender;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?:19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "Date of Birth must be in the format YYYY-MM-DD")
    private String dob;

    @NotNull
    @NotBlank
    private String profile;
    @NotNull
    @NotBlank
    private String bio;
}
