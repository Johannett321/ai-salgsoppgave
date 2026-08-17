package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(String ourID);

    Optional<User> findByFacebookId(String facebookId);
    Optional<User> findByGoogleSub(String googleSub);

    boolean existsByEmail(String email);
}
