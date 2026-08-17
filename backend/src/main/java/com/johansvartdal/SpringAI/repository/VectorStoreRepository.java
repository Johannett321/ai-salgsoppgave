package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.VectorStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStore, String> {
}
