package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bva_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private String id;
    private String facebookId;
    private String googleSub;

    private String email;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserSalgsoppgaveJob> salgsoppgaveJobber;

    public User(String email) {
        this.email = email;
    }
}
