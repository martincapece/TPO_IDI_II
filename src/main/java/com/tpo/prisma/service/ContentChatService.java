package com.tpo.prisma.service;

import com.tpo.prisma.dto.ChatMessage;
import com.tpo.prisma.model.ChatMongoDB;
import com.tpo.prisma.repository.ChatMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentChatService {

    @Autowired
    private ChatMongoRepository chatMongoRepository;

    /**
     * Envía un mensaje de chat para un Content (MongoDB)
     */
    public ChatMessage sendMessage(String chatId, String contentId, String userId, String username, String text) {
        ChatMongoDB chatMongo = new ChatMongoDB(chatId, contentId, userId, username, text);
        chatMongo = chatMongoRepository.save(chatMongo);
        
        return convertToDTO(chatMongo);
    }

    /**
     * Obtiene mensajes recientes de un chat de Content
     */
    public List<ChatMessage> getRecentMessages(String chatId, int limit) {
        List<ChatMongoDB> messages = chatMongoRepository.findByChatIdOrderByTimestampDesc(chatId);
        
        return messages.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Limpia el chat de un Content
     */
    @Transactional
    public void clearChat(String chatId) {
        chatMongoRepository.deleteByChatId(chatId);
    }

    /**
     * Convierte ChatMongoDB a DTO
     */
    private ChatMessage convertToDTO(ChatMongoDB mongo) {
        ChatMessage dto = new ChatMessage();
        dto.setUserId(mongo.getUserId());
        dto.setUsername(mongo.getUsername());
        dto.setText(mongo.getText());
        dto.setTimestamp(mongo.getTimestamp());
        return dto;
    }
}
