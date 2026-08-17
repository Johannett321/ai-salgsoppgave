package com.johansvartdal.SpringAI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "salgsoppgave")
public class Salgsoppgave {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String gateNavn;
    private String gateNummer; // string as it can be 18b
    private String postNummer;
    private String postSted;
    private Double longtitude;
    private Double latitude;

    private Double prisAntydning;
    private Double totalPris;
    private Double bruksAreal;
    private Double byggeAar;

    @OneToMany(mappedBy = "salgsoppgave")
    private List<SalgsoppgaveBemerkning> bemerkninger;

    @Column(columnDefinition = "TEXT")
    private String oppsummering;

}
