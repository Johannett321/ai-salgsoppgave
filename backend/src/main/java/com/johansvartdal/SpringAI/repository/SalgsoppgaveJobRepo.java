package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.enums.JobStatus;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalgsoppgaveJobRepo extends JpaRepository<SalgsoppgaveJob, String> {

    Optional<SalgsoppgaveJob> findFirstByFinnUrlOrderByStartDateDesc(String finnUrl);

    List<SalgsoppgaveJob> findAllByJobStatusNotIn(List<JobStatus> jobStatuses);
}
