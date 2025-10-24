package com.tpo.prisma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpo.prisma.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StreamingChatService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;
    
    private static final long CHAT_TTL_AFTER_END_MINUTES = 5;
    private static final int MAX_MESSAGES = 500;

    public StreamingChatService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public ChatMessage sendMessage(String chatId, String streamId, String userId, String username, String text) {
        ChatMessage message = new ChatMessage(userId, username, text);

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String redisKey = "chat:" + chatId + ":messages";
            
            // Agregar mensaje a la lista en Redis
            redisTemplate.opsForList().rightPush(redisKey, messageJson);
            
            // Mantener solo los últimos 500 mensajes
            redisTemplate.opsForList().trim(redisKey, -500, -1);
            
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar mensaje de chat", e);
        }
    }


    public List<ChatMessage> getRecentMessages(String chatId, int limit) {
        String redisKey = "chat:" + chatId + ":messages";
        
        List<String> messagesJson = redisTemplate.opsForList().range(redisKey, -limit, -1);
        
        if (messagesJson == null || messagesJson.isEmpty()) {
            return new ArrayList<>();
        }

        List<ChatMessage> messages = new ArrayList<>();
        for (String json : messagesJson) {
            try {
                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
                messages.add(message);
            } catch (JsonProcessingException e) {
                System.err.println("Error al deserializar mensaje: " + e.getMessage());
            }
        }
        
        return messages;
    }

    public void clearChat(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        redisTemplate.delete(redisKey);
    }
    
    public void setExpirationOnStreamEnd(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        redisTemplate.expire(redisKey, CHAT_TTL_AFTER_END_MINUTES, TimeUnit.MINUTES);
    }
    
    public Long getChatTTL(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
    }
}
