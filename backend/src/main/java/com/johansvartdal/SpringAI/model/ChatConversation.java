package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(mappedBy = "conversation")
    @OrderBy("timestamp ASC ")
    private List<ChatMessage> messages;

    @ManyToOne
    @JsonIgnore
    private User user;
}
