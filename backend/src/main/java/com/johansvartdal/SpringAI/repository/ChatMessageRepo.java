package com.johansvartdal.SpringAI.repository;

import com.johansvartdal.SpringAI.model.ChatMessage;
import com.johansvartdal.SpringAI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationUserOrderByTimestampDesc(User user);
}
