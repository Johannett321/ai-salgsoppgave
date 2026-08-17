package com.johansvartdal.SpringAI.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePasswordDTO {
    private String token;
    private String password;
}
