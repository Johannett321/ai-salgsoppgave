package com.johansvartdal.SpringAI.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {
    @Email
    @NotBlank
    @Size(min = 5, max = 255)
    private String email;
    private String oldPassword;
    private String newPassword;

    @NotBlank
    @Size(min = 2, max = 255)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 255)
    private String lastName;
}
