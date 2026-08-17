package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.model.User;
import com.johansvartdal.SpringAI.model.UserSalgsoppgaveJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSalgsoppgaveJobRepo extends JpaRepository<UserSalgsoppgaveJob, String> {

    UserSalgsoppgaveJob findByUserAndSalgsoppgaveJobId(User user, String salgsoppgaveJobId);

    Optional<UserSalgsoppgaveJob> findByUserAndSalgsoppgaveJob(User currentUser, SalgsoppgaveJob salgsoppgaveJob);
}
