package com.johansvartdal.SpringAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class UserSalgsoppgaveJob {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    private SalgsoppgaveJob salgsoppgaveJob;

    @ManyToOne
    @JoinColumn(name = "chat_conversation_id")
    private ChatConversation chatConversation;
}
