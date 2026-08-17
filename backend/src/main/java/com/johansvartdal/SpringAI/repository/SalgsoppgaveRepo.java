package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.Salgsoppgave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalgsoppgaveRepo extends JpaRepository<Salgsoppgave, String> {
}
