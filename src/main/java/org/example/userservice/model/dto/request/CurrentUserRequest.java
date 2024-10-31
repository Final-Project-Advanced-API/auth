package org.example.userservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Pattern(
            regexp = "^(19[0-9]{2}|20[0-2][0-9]|2030)-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "Date of Birth must be in the format YYYY-MM-DD and range from 1900-01-01 to 2030-12-31."
    )
    @Schema(example = "2000-05-30")
    private String dob;

    @NotNull
    @NotBlank
    @Pattern(regexp = "(\\S+(\\.(?i)(jpg|png|gif|bmp))$)",
            message = "profile must be contain file extension such as jpg, png, gif and bmp only"
    )
    private String profile;
    @NotNull
    @NotBlank
    private String bio;
}
