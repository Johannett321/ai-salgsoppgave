package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.PreRegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreRegisteredUserRepository extends JpaRepository<PreRegisteredUser, String> {

    Optional<PreRegisteredUser> findByEmail(String email);
}
