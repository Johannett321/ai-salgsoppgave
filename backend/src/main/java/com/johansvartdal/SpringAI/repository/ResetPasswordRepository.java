package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.ResetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPasswordRequest, String> {

}
