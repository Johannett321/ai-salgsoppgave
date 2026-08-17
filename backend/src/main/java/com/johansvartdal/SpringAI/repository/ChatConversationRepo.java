package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatConversationRepo extends JpaRepository<ChatConversation, String> {
}
