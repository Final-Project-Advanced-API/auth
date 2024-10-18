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
public class UserRequest {
//    @NotNull
//    @NotBlank
//    @Pattern(regexp = "^[a-zA-Z]+$", message = "Username must contain only alphabetic characters.")
//    private String username;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "FullName must contain only alphabetic characters.")
    private String fullName;

    @NotNull
    @NotBlank
    @Pattern(
            regexp = "(?i)^(male|female|other)$",
            message = "Please specify a valid gender (male, female, or other)"
    )
    private String gender;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?:19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "Date of Birth must be in the format YYYY-MM-DD")
    private String dob;

    @NotNull
    @NotBlank
    @Email
//    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|hotmail\\.com|outlook\\.com)$",
//            message = "Email must end with @gmail.com, @yahoo.com, @hotmail.com, or @outlook.com")
    @Pattern(regexp = "^[\\w.%+-]+@[\\w-]{1,5}\\.[a-zA-Z]{1,5}$", message = "Email must follow the format: user@1-5chars.1-5chars")
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and include both letters and numbers"
    )
    private String password;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and include both letters and numbers"
    )
    private String confirmPassword;
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
