package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.SalgsoppgaveBemerkning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalgsoppgaveBemerkningRepository extends JpaRepository<SalgsoppgaveBemerkning, String> {

}
