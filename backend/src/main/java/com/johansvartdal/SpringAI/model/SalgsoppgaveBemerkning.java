package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "salgsoppgave_bemerkning")
public class SalgsoppgaveBemerkning {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "TEXT")
    private String bemerkning;

    @ManyToOne
    @JsonIgnore
    private Salgsoppgave salgsoppgave;

    public SalgsoppgaveBemerkning(String bemerkning) {
        this.bemerkning = bemerkning;
    }
}
