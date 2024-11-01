package org.example.userservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserRequest {

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "FullName must contain only alphabetic characters.")
    @Schema(example = "Jonh Wick")
    private String fullName;

    @NotNull
    @NotBlank
    @Pattern(
            regexp = "(?i)^(male|female|other)$",
            message = "Please specify a valid gender (male, female, or other)"
    )
    @Schema(example = "male")
    private String gender;

    @NotNull
    @NotBlank
    @Schema(example = "2000-05-12")
    private String dob;

    @NotNull
    @NotBlank
    @Email
    @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Email must follow the format: user@domain.com")
    @Schema(example = "welcome@gmail.com")
    private String email;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and include both letters and numbers"
    )
    @Schema(example = "*****")
    private String password;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and include both letters and numbers"
    )
    @Schema(example = "*****")
    private String confirmPassword;

}
