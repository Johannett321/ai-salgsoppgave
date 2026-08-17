package com.johansvartdal.SpringAI.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vector_store")
public class VectorStore {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "content", length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private Object embedding;
}