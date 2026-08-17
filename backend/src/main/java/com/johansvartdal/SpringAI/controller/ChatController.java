package com.johansvartdal.SpringAI.controller;

import com.johansvartdal.SpringAI.model.ChatConversation;
import com.johansvartdal.SpringAI.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {


    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/search")
    public ResponseEntity<String> search(@RequestBody String question) {
        return ResponseEntity.ok(chatService.search(question));
    }

    @PostMapping("/job/{id}/question")
    public ResponseEntity<ChatConversation> askQuestion(@PathVariable String id, @RequestBody Map<String, String> map) {
        return ResponseEntity.ok(chatService.askQuestion(id, map.get("question")));
    }
}
