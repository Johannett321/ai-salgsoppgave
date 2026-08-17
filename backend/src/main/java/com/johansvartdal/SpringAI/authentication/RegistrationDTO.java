package com.johansvartdal.SpringAI.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationDTO {
    private String email;
    private String password;

    private String firstName;
    private String lastName;
}
