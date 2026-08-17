package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.ChatMessageRole;
import com.johansvartdal.SpringAI.exception.InsufficientQuotaException;
import com.johansvartdal.SpringAI.exception.UnauthorizedException;
import com.johansvartdal.SpringAI.model.ChatConversation;
import com.johansvartdal.SpringAI.model.ChatMessage;
import com.johansvartdal.SpringAI.model.User;
import com.johansvartdal.SpringAI.model.UserSalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.ChatConversationRepo;
import com.johansvartdal.SpringAI.repository.ChatMessageRepo;
import com.johansvartdal.SpringAI.repository.UserSalgsoppgaveJobRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final UserService userService;
    private final PgVectorStore vectorStore;
    private final ChatMessageRepo chatMessageRepo;
    private final ChatConversationRepo chatConversationRepo;
    private final UserSalgsoppgaveJobRepo userSalgsoppgaveJobRepo;
    private final AnthropicChatModel chatModel;

    public ChatService(UserService userService, PgVectorStore vectorStore, ChatMessageRepo chatMessageRepo, ChatConversationRepo chatConversationRepo, UserSalgsoppgaveJobRepo userSalgsoppgaveJobRepo, AnthropicChatModel chatModel) {
        this.userService = userService;
        this.vectorStore = vectorStore;
        this.chatMessageRepo = chatMessageRepo;
        this.chatConversationRepo = chatConversationRepo;
        this.userSalgsoppgaveJobRepo = userSalgsoppgaveJobRepo;
        this.chatModel = chatModel;
    }

    public String search(String question) {
        return vectorStore.similaritySearch(question).stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
    }

    public ChatConversation askQuestion(String salgsoppgaveJobId, String question) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("User not logged in");
        }

        Long messagesLastDay = chatMessageRepo.findByConversationUserOrderByTimestampDesc(user).stream()
                .limit(105)
                .filter(m -> m.getTimestamp().isAfter(LocalDateTime.now().minusDays(1)))
                .count();

        if (messagesLastDay >= 100) {
            throw new InsufficientQuotaException("Daily message quote used up");
        }

        log.info("Processing question '{}'", question);

        UserSalgsoppgaveJob userSalgsoppgaveJob = userSalgsoppgaveJobRepo.findByUserAndSalgsoppgaveJobId(user, salgsoppgaveJobId);
        ChatConversation conversation = userSalgsoppgaveJob.getChatConversation();
        if (conversation == null) {
            conversation = new ChatConversation();
            conversation.setMessages(new ArrayList<>());
            chatConversationRepo.save(conversation);
            userSalgsoppgaveJob.setChatConversation(conversation);
            userSalgsoppgaveJobRepo.save(userSalgsoppgaveJob);
        }

        ChatMessage questionMessage = new ChatMessage();
        questionMessage.setTimestamp(LocalDateTime.now());
        questionMessage.setRole(ChatMessageRole.USER);
        questionMessage.setMessage(question);
        questionMessage.setConversation(conversation);
        chatMessageRepo.save(questionMessage);
        conversation.getMessages().add(questionMessage);

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(3)
                .similarityThreshold(0.4)
                .filterExpression(
                        b.eq("salgsoppgaveJob", salgsoppgaveJobId).build())
                .build());

        if (documents.isEmpty()) {
            return userSalgsoppgaveJob.getChatConversation();
        }

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("""
                Svar på brukerens spørsmål om salgsoppgaven basert på følgende context:
                {context}
                """);

        systemPromptTemplate.add("context", documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"))
        );

        List<Message> previousMessages = new ArrayList<>();
        if (conversation.getMessages() != null) {
            conversation.getMessages()
                    .forEach(message -> {
                        Message previousMessage = switch (message.getRole()) {
                            case USER -> new UserMessage(message.getMessage());
                            case ASSISTANT -> new AssistantMessage(message.getMessage());
                        };

                        previousMessages.add(previousMessage);
                    });
        }

        UserMessage userMessage = new UserMessage(question);

        previousMessages.add(systemPromptTemplate.createMessage());
        previousMessages.add(userMessage);

        Prompt prompt = new Prompt(previousMessages);

        String response = chatModel.call(prompt).getResult().getOutput().getText();

        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setTimestamp(LocalDateTime.now());
        responseMessage.setRole(ChatMessageRole.ASSISTANT);
        responseMessage.setMessage(response);
        responseMessage.setConversation(conversation);
        chatMessageRepo.save(responseMessage);
        conversation.getMessages().add(responseMessage);

        return conversation;
    }
}
