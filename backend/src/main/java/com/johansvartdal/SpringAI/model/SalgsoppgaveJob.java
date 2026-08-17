package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.johansvartdal.SpringAI.enums.JobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALGSOPPGAVE_JOB")
public class SalgsoppgaveJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDateTime startDate;

    private JobStatus jobStatus;
    private String failedReason;

    private String finnUrl;
    private String finnKomplettSalgsoppgaveUrl;
    private String salgsoppgavePDFUrl;

    private String pdfPath;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String pdfContent;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String finnContent;

    @OneToOne
    private Salgsoppgave salgsoppgave;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalgsoppgaveJob that = (SalgsoppgaveJob) o;
        return Objects.equals(id, that.id); // compare relevant fields, e.g., id
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // hash based on relevant fields
    }
}
