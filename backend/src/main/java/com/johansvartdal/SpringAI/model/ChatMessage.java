package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.johansvartdal.SpringAI.enums.ChatMessageRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private ChatMessageRole role;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JsonIgnore
    private ChatConversation conversation;
}
